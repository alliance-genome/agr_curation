package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.model.entities.interfaces.CurieCarrier;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;

/**
 * SCRUM-6359 / SCRUM-6360 — AGRKB curie minting for every entity that carries a curie. Replaces the
 * per-class helpers written for disease annotations (SCRUM-6078 / SCRUM-6170) and alleles
 * (SCRUM-6173), which were the same logic twice.
 *
 * Two entry points:
 *
 * 1. {@link #mintCurieIfAbsent} on the create/upsert paths, for one entity at a time.
 * 2. {@link #mintMissingCuries} for the one-time backfill of rows that predate minting.
 *
 * The persistence lives in {@link BaseCurieSQLDAO}, which every curie-carrying DAO extends, so this
 * class holds only the MaTI interaction and the batch orchestration.
 */
@Log4j2
@RequestScoped
public class CurieMintService {

	@Inject MatiService matiService;

	/**
	 * Mints and assigns a single AGRKB curie to {@code entity} iff it does not already have one,
	 * returning {@code true} if a curie was minted.
	 *
	 * Call this immediately before the entity is persisted, so the curie is written in the caller's
	 * transaction — this method deliberately has no {@code @Transactional} of its own. A re-load
	 * resolves to the managed entity, which already carries its curie, so this is a no-op for it and
	 * the AGRKB id stays stable across loads.
	 *
	 * On a shared create/update path, guard the call so it only runs for new entities. Validators
	 * that serve both (e.g. {@code AlleleValidator.validateAllele}) assign
	 * {@code setCurie(handleStringField(uiEntity.getCurie()))} unconditionally from the payload, so
	 * an update that omits curie nulls it — and an unguarded mint would then issue a fresh one,
	 * silently changing the entity's AGRKB id.
	 *
	 * Crash safety: MaTI advances its sequence at the POST inside {@link MatiService#mintCurie}. If
	 * the caller's transaction rolls back after this returns, the minted curie is burned (a gap in
	 * the AGRKB sequence). AGRKB ids are not required to be gapless, so this is acceptable.
	 *
	 * Availability: minting must never block a create or upsert. If MaTI is unreachable the entity
	 * is persisted without a curie and a NULL is left for the next re-load or the backfill to fill
	 * in — both target NULL-curie rows. This also keeps the integration tests, which run without a
	 * MaTI server, green.
	 */
	public boolean mintCurieIfAbsent(CurieCarrier entity, MatiSubdomain subdomain) {
		if (entity == null || entity.getCurie() != null) {
			return false;
		}
		try {
			entity.setCurie(matiService.mintCurie(subdomain.getSubdomainName()));
			return true;
		} catch (Exception e) {
			log.warn("Failed to mint AGRKB curie in subdomain " + subdomain.getSubdomainName()
				+ "; persisting without one (curie will be backfilled on the next re-load)", e);
			return false;
		}
	}

	/**
	 * Backfill: assigns AGRKB curies to every row of the DAO's entity type whose curie is NULL.
	 *
	 * Idempotent — only NULL-curie rows are touched, so re-running after a partial run, or after new
	 * rows have been loaded without curies, safely picks up where it left off. Resumable — each
	 * batch is committed by {@link BaseCurieSQLDAO#assignCuries}, so an interrupted run loses at
	 * most the batch in flight.
	 *
	 * Crash safety: MaTI advances its sequence at the POST. If the process dies after that POST but
	 * before the assignments commit, those curies are lost and the rows are re-handled on the next
	 * run with a fresh batch. Keep the batch small to bound the blast radius.
	 *
	 * @param dao       the DAO for the entity type being backfilled
	 * @param subdomain the MaTI subdomain to mint from
	 * @param batchSize how many rows to mint per batch, and so per MaTI call and per transaction
	 * @param maxToMint hard cap on the TOTAL minted in this call; 0 means no cap. Bounding the total
	 *                  lets an operator work a large backfill through the table in safe chunks
	 *                  instead of one run that could overwhelm the environment.
	 */
	public void mintMissingCuries(BaseCurieSQLDAO<?> dao, MatiSubdomain subdomain, int batchSize, int maxToMint) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must be > 0, got " + batchSize);
		}
		if (maxToMint < 0) {
			throw new IllegalArgumentException("maxToMint must be >= 0 (0 = no cap), got " + maxToMint);
		}

		long totalMissing = dao.countMissingCuries();
		// maxToMint == 0 means mint everything that is missing.
		long target = maxToMint == 0 ? totalMissing : Math.min(totalMissing, maxToMint);

		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		pdh.startProcess(subdomain.getSubdomainName() + " AGRKB curie mint", target);

		long minted = 0;
		// Cursor carried across batches so each fetch is proportional to the batch size rather than
		// re-scanning from the top of the table — see BaseCurieSQLDAO.findIdsMissingCuries.
		long lastId = 0;
		while (minted < target) {
			// Never fetch more than the remaining allowance towards the cap.
			int thisBatch = (int) Math.min(batchSize, target - minted);
			List<Long> batchIds = dao.findIdsMissingCuries(thisBatch, lastId);
			if (batchIds.isEmpty()) {
				break;
			}
			lastId = batchIds.getLast();

			List<String> curies = matiService.mintCuries(subdomain.getSubdomainName(), batchIds.size());

			dao.assignCuries(batchIds, curies);

			minted += batchIds.size();
			for (int i = 0; i < batchIds.size(); i++) {
				pdh.progressProcess();
			}
		}

		pdh.finishProcess();
	}
}
