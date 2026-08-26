package org.alliancegenome.curation_api.services.helpers.alleles;

import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.services.MatiService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;

/**
 * SCRUM-6173 — assigns AGRKB curies (sourced from MaTI subdomain {@code allele}, code 106)
 * to alleles: one at a time on the create/upsert paths, and in bulk for the one-time
 * backfill of rows that predate minting.
 *
 * Mirrors {@link org.alliancegenome.curation_api.services.helpers.annotations.DiseaseAnnotationCurieMintHelper}
 * (SCRUM-6078 / SCRUM-6170). Two differences worth knowing:
 *
 * 1. Where the mint happens. The disease-annotation services own both the transaction and
 *    the {@code persist(...)} call, so minting sits in the service between validator and
 *    persist. Alleles persist inside their validators
 *    ({@code AlleleValidator.validateAlleleCreate}, {@code AlleleDTOValidator.validateAlleleDTO}),
 *    and {@code AlleleService.upsert} is not {@code @Transactional} — the validator's own
 *    transaction has already committed by the time the service method resumes, so minting
 *    there would set a field on a detached entity and silently do nothing. Hence
 *    {@link #mintCurieIfAbsent(Allele)} is called from the validators, immediately before
 *    they persist.
 *
 * 2. Scale. Every allele is in scope regardless of {@code obsolete} / {@code internal}, so
 *    the backfill covers ~3.7M rows (vs ~96K disease annotations). The batch fetch lives in
 *    {@link org.alliancegenome.curation_api.dao.AlleleDAO#findIdsMissingCuries(int, long)} and is
 *    driven from {@code allele} with a forward id cursor, so no extra index is needed: the allele
 *    primary key drives the scan. A partial index on {@code biologicalentity (id) WHERE curie IS
 *    NULL} would cover ~32M rows this job never touches (biologicalentity is ~35.9M rows, of which
 *    exon and codingsequence alone are ~27M) and is not worth creating.
 */
@Log4j2
@RequestScoped
public class AlleleCurieMintHelper {

	@Inject AlleleDAO alleleDAO;
	@Inject MatiService matiService;

	/**
	 * Mints and assigns a single AGRKB curie to {@code allele} iff it does not already have
	 * one, returning {@code true} if a curie was minted.
	 *
	 * Called from the allele validators just before the entity is persisted, so the curie is
	 * written in the caller's transaction (no {@code @Transactional} of its own here). A
	 * re-load resolves to the managed entity, which already carries its curie, so this is a
	 * no-op for it — the AGRKB id stays stable across loads. {@code AlleleDTO} carries no curie
	 * field and the DTO validator chain never assigns one, so a load cannot clobber an existing
	 * id either.
	 *
	 * The UI path is different and the caller must guard it: {@code AlleleValidator.validateAllele}
	 * is shared by create and update, and its field-copy chain assigns
	 * {@code setCurie(handleStringField(uiEntity.getCurie()))} unconditionally, so an update whose
	 * payload omits curie nulls it. {@code AlleleValidator} therefore calls this only when
	 * {@code dbEntity.getId() == null}; without that guard an allele's AGRKB id would silently
	 * change on every such update.
	 *
	 * No {@code obsolete} / {@code internal} check: every allele gets an id, and an allele
	 * obsoleted later keeps the one it already holds.
	 *
	 * Crash safety: MaTI advances its sequence at the POST inside
	 * {@link MatiService#mintCurie}. If the caller's transaction rolls back after this
	 * returns, the minted curie is burned (a gap in the AGRKB sequence). AGRKB ids are not
	 * required to be gapless, so this is acceptable.
	 *
	 * Availability: minting must never block allele create/upsert. If MaTI is unreachable
	 * (or otherwise fails), the allele is persisted without a curie and a {@code NULL} curie
	 * is left for the next re-load or the backfill to fill in — both target
	 * {@code NULL}-curie rows. This also keeps the integration tests (which run without a
	 * MaTI server) green.
	 */
	public boolean mintCurieIfAbsent(Allele allele) {
		if (allele == null || allele.getCurie() != null) {
			return false;
		}
		try {
			allele.setCurie(matiService.mintCurie(MatiService.SUBDOMAIN_ALLELE));
			return true;
		} catch (Exception e) {
			log.warn("Failed to mint AGRKB curie for allele; persisting without one "
				+ "(curie will be backfilled on the next re-load)", e);
			return false;
		}
	}

	/**
	 * One-time backfill that assigns AGRKB curies to every allele whose curie is currently
	 * NULL.
	 *
	 * Idempotent: only rows with NULL curie are touched, so re-running after a partial run
	 * (or after new alleles have been loaded without curies) safely picks up where it left
	 * off.
	 *
	 * Crash safety: MaTI advances its sequence at the POST. If this process dies after that
	 * POST but before the assignments are committed, those curies are lost — the
	 * corresponding alleles will be re-handled on the next run with a fresh batch. Keep the
	 * batch small to bound the blast radius.
	 *
	 * @param batchSize how many alleles to mint per batch/MaTI call
	 * @param maxToMint hard cap on the TOTAL number of alleles minted in this call;
	 *        {@code 0} means no cap (mint every NULL-curie row). Bounding the total lets an
	 *        operator work this ~3.7M row backfill through the table in safe chunks instead
	 *        of in one run that could overwhelm the environment. Idempotent, so repeated
	 *        capped calls pick up where the previous one left off.
	 */
	public void mintMissingCuries(int batchSize, int maxToMint) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must be > 0, got " + batchSize);
		}
		if (maxToMint < 0) {
			throw new IllegalArgumentException("maxToMint must be >= 0 (0 = no cap), got " + maxToMint);
		}

		long totalMissing = alleleDAO.countMissingCuries();
		// maxToMint == 0 means mint everything that is missing.
		long target = maxToMint == 0 ? totalMissing : Math.min(totalMissing, maxToMint);

		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		pdh.startProcess("Allele AGRKB curie mint", target);

		long minted = 0;
		// Cursor carried across batches so each fetch is proportional to the batch size rather than
		// re-scanning from the top of the table — see AlleleDAO.findIdsMissingCuries.
		long lastId = 0;
		while (minted < target) {
			// Never fetch more than the remaining allowance towards the cap.
			int thisBatch = (int) Math.min(batchSize, target - minted);
			List<Long> batchIds = alleleDAO.findIdsMissingCuries(thisBatch, lastId);
			if (batchIds.isEmpty()) {
				break;
			}
			lastId = batchIds.get(batchIds.size() - 1);

			List<String> curies = matiService.mintCuries(MatiService.SUBDOMAIN_ALLELE, batchIds.size());

			alleleDAO.assignCuries(batchIds, curies);

			minted += batchIds.size();
			for (int i = 0; i < batchIds.size(); i++) {
				pdh.progressProcess();
			}
		}

		pdh.finishProcess();
	}

}
