package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class HTPExpressionDatasetAnnotationDAO extends BaseSQLDAO<HTPExpressionDatasetAnnotation> {

	private static final int QUERY_BATCH_SIZE = 5000;

	protected HTPExpressionDatasetAnnotationDAO() {
		super(HTPExpressionDatasetAnnotation.class);
	}

	public List<Long> getAllHTPDatasetSearchResultIds() {
		String sql = """
				SELECT id
				FROM htpexpressiondatasetannotation
				WHERE obsolete = false
				AND internal = false
				ORDER BY id
				""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<HTPExpressionDatasetAnnotation> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}

		// Query 1: Main entities with single-valued associations + categoryTags (one bag)
		List<HTPExpressionDatasetAnnotation> results = entityManager.createQuery("""
				SELECT DISTINCT h FROM HTPExpressionDatasetAnnotation h
				LEFT JOIN FETCH h.htpExpressionDataset
				LEFT JOIN FETCH h.dataProvider
				LEFT JOIN FETCH h.relatedNote
				LEFT JOIN FETCH h.categoryTags
				WHERE h.id IN :ids
				""", HTPExpressionDatasetAnnotation.class)
			.setParameter("ids", ids)
			.getResultList();

		List<Long> datasetEntityIds = results.stream()
			.filter(h -> h.getHtpExpressionDataset() != null)
			.map(h -> h.getHtpExpressionDataset().getId())
			.distinct()
			.collect(Collectors.toList());

		if (datasetEntityIds.isEmpty()) {
			return results;
		}

		// Query 2: ExternalDataBaseEntity with preferredCrossReference + crossReferences (one bag)
		entityManager.createQuery("""
				SELECT DISTINCT e FROM ExternalDataBaseEntity e
				LEFT JOIN FETCH e.preferredCrossReference
				LEFT JOIN FETCH e.crossReferences
				WHERE e.id IN :datasetIds
				""", ExternalDataBaseEntity.class)
			.setParameter("datasetIds", datasetEntityIds)
			.getResultList();

		// Query 2b: Initialize the inverse htpExpressionDatasetSampleAnnotation collection on
		// ExternalDataBaseEntity (separate query because it's a second bag on the same entity)
		entityManager.createQuery("""
				SELECT DISTINCT e FROM ExternalDataBaseEntity e
				LEFT JOIN FETCH e.htpExpressionDatasetSampleAnnotation
				WHERE e.id IN :datasetIds
				""", ExternalDataBaseEntity.class)
			.setParameter("datasetIds", datasetEntityIds)
			.getResultList();

		// Query 3: Sample annotations with single-valued associations
		List<HTPExpressionDatasetSampleAnnotation> sampleAnnotations = entityManager.createQuery("""
				SELECT DISTINCT s FROM HTPExpressionDatasetSampleAnnotation s
				LEFT JOIN FETCH s.taxon
				LEFT JOIN FETCH s.geneticSex
				LEFT JOIN FETCH s.htpExpressionSample
				LEFT JOIN FETCH s.expressionAssayUsed
				JOIN s.datasetIds d
				WHERE d.id IN :datasetIds
				""", HTPExpressionDatasetSampleAnnotation.class)
			.setParameter("datasetIds", datasetEntityIds)
			.getResultList();

		if (sampleAnnotations.isEmpty()) {
			return results;
		}

		List<Long> sampleIds = sampleAnnotations.stream()
			.map(HTPExpressionDatasetSampleAnnotation::getId)
			.distinct()
			.collect(Collectors.toList());

		// Query 4: Sample annotation locations (bag collection, separate query, batched)
		List<HTPExpressionDatasetSampleAnnotation> samplesWithLocations = executeInBatches(sampleIds, batch ->
			entityManager.createQuery("""
					SELECT DISTINCT s FROM HTPExpressionDatasetSampleAnnotation s
					LEFT JOIN FETCH s.htpExpressionSampleLocations
					WHERE s.id IN :sampleIds
					""", HTPExpressionDatasetSampleAnnotation.class)
				.setParameter("sampleIds", batch)
				.getResultList()
		);

		List<Long> locationIds = samplesWithLocations.stream()
			.filter(s -> s.getHtpExpressionSampleLocations() != null)
			.flatMap(s -> s.getHtpExpressionSampleLocations().stream())
			.map(AnatomicalSite::getId)
			.distinct()
			.collect(Collectors.toList());

		if (!locationIds.isEmpty()) {
			// Query 5: AnatomicalSite with anatomicalStructure + uberonTerms (one bag, batched)
			List<AnatomicalSite> sites = executeInBatches(locationIds, batch ->
				entityManager.createQuery("""
						SELECT DISTINCT a FROM AnatomicalSite a
						LEFT JOIN FETCH a.anatomicalStructure
						LEFT JOIN FETCH a.anatomicalStructureUberonTerms
						WHERE a.id IN :locationIds
						""", AnatomicalSite.class)
					.setParameter("locationIds", batch)
					.getResultList()
			);

			// Query 6: Pre-load ancestors for anatomical structures (ancestors is a Set, not a bag, batched)
			List<Long> anatomicalTermIds = sites.stream()
				.filter(a -> a.getAnatomicalStructure() != null)
				.map(a -> a.getAnatomicalStructure().getId())
				.distinct()
				.collect(Collectors.toList());

			if (!anatomicalTermIds.isEmpty()) {
				executeInBatches(anatomicalTermIds, batch ->
					entityManager.createQuery("""
							SELECT DISTINCT at FROM AnatomicalTerm at
							LEFT JOIN FETCH at.ancestors anc
							LEFT JOIN FETCH anc.closureObject
							WHERE at.id IN :termIds
							""", AnatomicalTerm.class)
						.setParameter("termIds", batch)
						.getResultList()
				);
			}
		}

		// Query 7: expressionAssayUsed synonyms (bag on OntologyTerm)
		List<Long> assayTermIds = sampleAnnotations.stream()
			.filter(s -> s.getExpressionAssayUsed() != null)
			.map(s -> s.getExpressionAssayUsed().getId())
			.distinct()
			.collect(Collectors.toList());

		if (!assayTermIds.isEmpty()) {
			entityManager.createQuery("""
					SELECT DISTINCT m FROM MMOTerm m
					LEFT JOIN FETCH m.synonyms
					WHERE m.id IN :termIds
					""", MMOTerm.class)
				.setParameter("termIds", assayTermIds)
				.getResultList();
		}

		return results;
	}

	// PostgreSQL PreparedStatements have a 65,535 parameter limit. When fetching
	// 1,500 dataset annotations, the related sample annotation IDs can exceed 27K,
	// which blows past this limit in JOIN FETCH queries. This method chunks large
	// ID lists into batches to stay within the parameter limit while still warming
	// Hibernate's L1 cache for the document builder.
	private <T> List<T> executeInBatches(List<Long> ids, Function<List<Long>, List<T>> queryFunction) {
		if (ids.size() <= QUERY_BATCH_SIZE) {
			return queryFunction.apply(ids);
		}
		List<T> allResults = new ArrayList<>();
		for (int i = 0; i < ids.size(); i += QUERY_BATCH_SIZE) {
			List<Long> batch = ids.subList(i, Math.min(i + QUERY_BATCH_SIZE, ids.size()));
			allResults.addAll(queryFunction.apply(batch));
		}
		return allResults;
	}
}
