package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}

	/**
	 * SCRUM-6078 — how many disease annotations still lack an AGRKB curie.
	 *
	 * Unlike Allele, curie is a column on the diseaseannotation table itself: DiseaseAnnotation
	 * extends Annotation rather than SubmittedObject, so there is no join to biologicalentity here.
	 */
	public long countMissingCuries() {
		String sql = """
				SELECT COUNT(*)
				FROM diseaseannotation
				WHERE curie is NULL
			""";

		Query query = entityManager.createNativeQuery(sql);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * SCRUM-6078 — the next {@code batchSize} disease annotation ids that lack an AGRKB curie, in
	 * id order, starting after {@code afterId}.
	 *
	 * The cursor keeps each batch proportional to the batch size. Without it the query restarts its
	 * scan at the top of the table for every batch, so the work grows with the number of batches
	 * already completed and the final empty batch that ends the run walks the whole table. That is
	 * tolerable at the ~96K rows this backfill faced, but the shape is shared with the allele
	 * backfill (~3.7M rows) and the remaining SCRUM-6358 classes, so both use the cursor.
	 *
	 * Pass 0 to start from the beginning. Rows skipped within a run (e.g. an annotation that
	 * acquired a curie between the SELECT and the assign) are not revisited by that run because the
	 * cursor has moved past them; the next run starts from 0 again and picks them up.
	 */
	public List<Long> findIdsMissingCuries(int batchSize, long afterId) {
		String sql = """
				SELECT id
				FROM diseaseannotation
				WHERE curie is NULL and id > :afterId
				ORDER BY id
				LIMIT :limit
			""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("afterId", afterId);
		query.setParameter("limit", batchSize);

		List<Object> objects = query.getResultList();
		List<Long> ids = new ArrayList<>();
		objects.forEach(object -> ids.add(((Number) object).longValue()));

		return ids;
	}

	/**
	 * SCRUM-6078 — assigns the given AGRKB curies to the given annotation ids, positionally, in a
	 * single transaction. Used by the backfill; each batch commits on its own so a long run is
	 * resumable.
	 *
	 * Each annotation is re-checked before assignment: a row may have disappeared, or acquired a
	 * curie of its own, between the id fetch and here. dateUpdated is refreshed via the standard
	 * merge path so audit triggers behave normally.
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
			DiseaseAnnotation da = find(id);
			if (da == null) {
				Log.warn("DA id=" + id + " disappeared between SELECT and assign; curie " + curie + " unused");
				continue;
			}
			if (da.getCurie() != null) {
				Log.warn("DA id=" + id + " already has curie=" + da.getCurie() + "; skipping mint " + curie);
				continue;
			}
			da.setCurie(curie);
			merge(da);
		}
	}
}
