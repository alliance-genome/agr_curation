package org.alliancegenome.curation_api.services.helpers.annotations;

import java.util.List;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.services.MatiService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
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
 */
@Log4j2
@RequestScoped
public class DiseaseAnnotationCurieMintHelper {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject MatiService matiService;
	@Inject EntityManager entityManager;

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
	 */
	public boolean mintCurieIfAbsent(DiseaseAnnotation annotation) {
		if (annotation == null || annotation.getCurie() != null) {
			return false;
		}
		annotation.setCurie(matiService.mintCurie(MatiService.SUBDOMAIN_DISEASE_ANNOTATION));
		return true;
	}

	public void mintMissingCuries(int batchSize) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must be > 0, got " + batchSize);
		}

		long totalMissing = countMissing();
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		pdh.startProcess("DiseaseAnnotation AGRKB curie mint", totalMissing);

		while (true) {
			List<Long> batchIds = findMissingCurieIds(batchSize);
			if (batchIds.isEmpty()) {
				break;
			}

			List<String> curies = matiService.mintCuries(
				MatiService.SUBDOMAIN_DISEASE_ANNOTATION,
				batchIds.size());

			assignCuries(batchIds, curies);

			for (int i = 0; i < batchIds.size(); i++) {
				pdh.progressProcess();
			}
		}

		pdh.finishProcess();
	}

	private long countMissing() {
		Query q = entityManager.createNativeQuery(
			"SELECT COUNT(*) FROM diseaseannotation WHERE curie IS NULL");
		return ((Number) q.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	private List<Long> findMissingCurieIds(int batchSize) {
		Query q = entityManager.createNativeQuery(
			"SELECT id FROM diseaseannotation WHERE curie IS NULL ORDER BY id LIMIT :limit");
		q.setParameter("limit", batchSize);
		List<Number> rows = q.getResultList();
		return rows.stream().map(Number::longValue).toList();
	}

	/**
	 * Persists the assignment in a single transaction. Each annotation's
	 * dateUpdated is refreshed via the standard merge path so audit
	 * triggers behave normally.
	 */
	@Transactional
	public void assignCuries(List<Long> ids, List<String> curies) {
		if (ids.size() != curies.size()) {
			throw new IllegalStateException(
				"id/curie size mismatch: ids=" + ids.size() + " curies=" + curies.size());
		}
		for (int i = 0; i < ids.size(); i++) {
			Long id = ids.get(i);
			String curie = curies.get(i);
			DiseaseAnnotation da = diseaseAnnotationDAO.find(id);
			if (da == null) {
				log.warn("DA id={} disappeared between SELECT and assign; curie {} unused", id, curie);
				continue;
			}
			if (da.getCurie() != null) {
				log.warn("DA id={} already has curie={}; skipping mint {}", id, da.getCurie(), curie);
				continue;
			}
			da.setCurie(curie);
			diseaseAnnotationDAO.merge(da);
		}
	}
}
