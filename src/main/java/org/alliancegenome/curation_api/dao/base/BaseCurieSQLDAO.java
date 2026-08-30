package org.alliancegenome.curation_api.dao.base;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.interfaces.CurieInterface;

import io.quarkus.logging.Log;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

/**
 * SCRUM-6359 / SCRUM-6360 — the curie-minting persistence shared by every entity that carries an
 * AGRKB curie. A DAO gains all of it by extending this instead of {@link BaseSQLDAO} directly.
 *
 * Queries are built with the Criteria API over {@code myClass}, not native SQL over a table name.
 * That is deliberate: the curie column lives in nine different tables (biologicalentity, reagent,
 * phenotypeannotation,
 * diseaseannotation, geneexpressionannotation, geneexpressionexperiment, informationcontententity,
 * ontologyterm, externaldatabaseentity) because each entity inherits the field from whichever
 * ancestor declares it, under a JOINED inheritance strategy. Hand-written SQL has to get that
 * mapping right once per class and it has already been got wrong once — the first run of the allele
 * backfill failed with {@code column "curie" does not exist} because the query read
 * {@code FROM allele}, while Allele keeps its curie on biologicalentity. Expressed over the entity
 * model there is nothing to get wrong, and one implementation serves all sixteen classes.
 */
public abstract class BaseCurieSQLDAO<E extends AuditedObject & CurieInterface> extends BaseSQLDAO<E> {

	protected BaseCurieSQLDAO(Class<E> myClass) {
		super(myClass);
	}

	/** How many rows of this entity type still lack an AGRKB curie. */
	public long countMissingCuries() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<E> root = query.from(myClass);
		query.select(cb.count(root)).where(cb.isNull(root.get("curie")));
		return entityManager.createQuery(query).getSingleResult();
	}

	/**
	 * The next {@code batchSize} ids of this entity type that lack an AGRKB curie, in id order,
	 * starting after {@code afterId}.
	 *
	 * The cursor keeps each fetch proportional to the batch size. Without it the query restarts its
	 * scan at the top of the table on every batch, so the cost grows with the number of batches
	 * already completed and the final empty batch that ends a run walks the whole table. It also
	 * means no supporting index is needed: the scan is driven by the primary key.
	 *
	 * Pass 0 to start from the beginning. Rows skipped within a run (one that acquired a curie
	 * between this fetch and the assign) are not revisited by that run, because the cursor has moved
	 * past them; the next run starts from 0 again and picks them up.
	 */
	public List<Long> findIdsMissingCuries(int batchSize, long afterId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<E> root = query.from(myClass);
		query.select(root.get("id"))
			.where(cb.isNull(root.get("curie")), cb.greaterThan(root.get("id"), afterId))
			.orderBy(cb.asc(root.get("id")));
		return entityManager.createQuery(query).setMaxResults(batchSize).getResultList();
	}

	/**
	 * Assigns the given AGRKB curies to the given ids, positionally, in a single transaction. Each
	 * batch commits on its own so a long backfill is resumable.
	 *
	 * Every row is re-checked before assignment: it may have disappeared, or acquired a curie of its
	 * own, between the id fetch and here. dateUpdated is refreshed via the standard merge path so
	 * audit triggers behave normally.
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
			E entity = find(id);
			if (entity == null) {
				Log.warn(myClass.getSimpleName() + " id=" + id + " disappeared between SELECT and assign; curie " + curie + " unused");
				continue;
			}
			if (entity.getCurie() != null) {
				Log.warn(myClass.getSimpleName() + " id=" + id + " already has curie=" + entity.getCurie() + "; skipping mint " + curie);
				continue;
			}
			entity.setCurie(curie);
			merge(entity);
		}
	}
}
