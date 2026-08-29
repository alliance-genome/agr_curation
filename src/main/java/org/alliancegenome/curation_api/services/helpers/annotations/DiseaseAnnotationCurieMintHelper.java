package org.alliancegenome.curation_api.services.helpers.annotations;

import java.util.List;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.services.MatiService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;

/**
 * SCRUM-6078 — one-time backfill that assigns AGRKB curies (sourced from
 * MaTI) to every DiseaseAnnotation row whose curie is currently NULL.
 *
 * Idempotent: only rows with NULL curie are touched, so re-running this
 * helper after a partial run (or after new annotations have been loaded
 * without curies) safely picks up where it left off.
 *
 * Crash safety: MaTI advances its sequence at the POST. If this process
 * dies after that POST but before the assignments are committed, those
 * curies are lost — the corresponding annotations will be re-handled on
 * the next run with a fresh batch. Keep the batch small to bound the
 * blast radius.
 *
 * Status: the backfill has completed on alpha, beta and production (0 NULL-curie annotations
 * on each as of 2026-08-26), so {@link #mintMissingCuries(int, int)} and
 * {@code /system/mintdacuries} have served their purpose and could be dropped. They are kept
 * deliberately: SCRUM-6358 registered subdomains for 16 classes, and this pair plus
 * {@link org.alliancegenome.curation_api.services.helpers.alleles.AlleleCurieMintHelper} are
 * the template the remaining backfills are built from. {@link #mintCurieIfAbsent} is
 * permanent either way — new annotations need it on every create and load.
 */
@Log4j2
@RequestScoped
public class DiseaseAnnotationCurieMintHelper {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject MatiService matiService;

	/**
	 * SCRUM-6170 — mints and assigns a single AGRKB curie to {@code annotation}
	 * iff it does not already have one, returning {@code true} if a curie was
	 * minted.
	 *
	 * Called from the disease-annotation create/upsert paths just before the
	 * entity is persisted, so the curie is written in the caller's transaction
	 * (no {@code @Transactional} of its own here). Re-loads of an existing
	 * annotation resolve to the managed entity, which already carries its curie,
	 * so this is a no-op for them — the AGRKB id stays stable across loads.
	 *
	 * Crash safety: MaTI advances its sequence at the POST inside
	 * {@link MatiService#mintCuries}. If the caller's transaction rolls back
	 * after this returns, the minted curie is burned (a gap in the AGRKB
	 * sequence). AGRKB ids are not required to be gapless, so this is acceptable.
	 *
	 * Availability: minting must never block annotation create/upsert. If MaTI
	 * is unreachable (or otherwise fails), the annotation is persisted without a
	 * curie and a {@code NULL} curie is left for the next re-load or the
	 * SCRUM-6078 backfill to fill in — both target {@code NULL}-curie rows. This
	 * also keeps the integration tests (which run without a MaTI server) green.
	 */
	public boolean mintCurieIfAbsent(DiseaseAnnotation annotation) {
		if (annotation == null || annotation.getCurie() != null) {
			return false;
		}
		try {
			annotation.setCurie(matiService.mintCurie(MatiService.SUBDOMAIN_DISEASE_ANNOTATION));
			return true;
		} catch (Exception e) {
			log.warn("Failed to mint AGRKB curie for disease annotation; persisting without one "
				+ "(curie will be backfilled on the next re-load)", e);
			return false;
		}
	}

	/**
	 * @param batchSize how many annotations to mint per batch/MaTI call
	 * @param maxToMint hard cap on the TOTAL number of annotations minted in
	 *        this call; {@code 0} means no cap (mint every NULL-curie row).
	 *        Bounding the total lets an operator work a large cold backfill
	 *        through the table in safe chunks instead of in one run that could
	 *        overwhelm the environment. Idempotent, so repeated capped calls
	 *        pick up where the previous one left off.
	 */
	public void mintMissingCuries(int batchSize, int maxToMint) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must be > 0, got " + batchSize);
		}
		if (maxToMint < 0) {
			throw new IllegalArgumentException("maxToMint must be >= 0 (0 = no cap), got " + maxToMint);
		}

		long totalMissing = diseaseAnnotationDAO.countMissingCuries();
		// maxToMint == 0 means mint everything that is missing.
		long target = maxToMint == 0 ? totalMissing : Math.min(totalMissing, maxToMint);

		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		pdh.startProcess("DiseaseAnnotation AGRKB curie mint", target);

		long minted = 0;
		// Cursor carried across batches so each fetch is proportional to the batch size rather than
		// re-scanning from the top of the table — see DiseaseAnnotationDAO.findIdsMissingCuries.
		long lastId = 0;
		while (minted < target) {
			// Never fetch more than the remaining allowance towards the cap.
			int thisBatch = (int) Math.min(batchSize, target - minted);
			List<Long> batchIds = diseaseAnnotationDAO.findIdsMissingCuries(thisBatch, lastId);
			if (batchIds.isEmpty()) {
				break;
			}
			lastId = batchIds.get(batchIds.size() - 1);

			List<String> curies = matiService.mintCuries(
				MatiService.SUBDOMAIN_DISEASE_ANNOTATION,
				batchIds.size());

			diseaseAnnotationDAO.assignCuries(batchIds, curies);

			minted += batchIds.size();
			for (int i = 0; i < batchIds.size(); i++) {
				pdh.progressProcess();
			}
		}

		pdh.finishProcess();
	}

}
