package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.document.es.ModelSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ApplicationScoped
public class AffectedGenomicModelDAO extends BaseSQLDAO<AffectedGenomicModel> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;

	protected AffectedGenomicModelDAO() {
		super(AffectedGenomicModel.class);
	}

	public Boolean hasReferencingDiseaseAnnotations(Long agmId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DA_SUBJECT + ".id", agmId);
		List<Long> results = agmDiseaseAnnotationDAO.findIdsByParams(params);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> sbParams = new HashMap<>();
		sbParams.put(EntityFieldConstants.STRAIN_BACKGROUND + ".id", agmId);
		results = geneDiseaseAnnotationDAO.findIdsByParams(sbParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> dgmParams = new HashMap<>();
		dgmParams.put(EntityFieldConstants.DA_MODIFIER_AGMS + ".id", agmId);
		results = diseaseAnnotationDAO.findIdsByParams(dgmParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long agmId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.PA_SUBJECT + ".id", agmId);
		List<Long> results = agmPhenotypeAnnotationDAO.findIdsByParams(params);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> sbParams = new HashMap<>();
		sbParams.put(EntityFieldConstants.STRAIN_BACKGROUND + ".id", agmId);
		results = genePhenotypeAnnotationDAO.findIdsByParams(sbParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingHTPExpressionDatasetSampleAnnotation(Long agmId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.GENOMIC_INFORMATION_AGM + ".id", agmId);
		List<Long> results = htpExpressionDatasetSampleAnnotationDAO.findIdsByParams(params);

		return CollectionUtils.isNotEmpty(results);
	}

	public List<Long> getAllIds() {
		String sql = """
				SELECT a.id
				FROM affectedgenomicmodel a
				INNER JOIN biologicalentity b ON b.id = a.id AND b.obsolete = false AND b.internal = false
				ORDER BY a.id
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<AffectedGenomicModel> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		return entityManager.createQuery(
				"SELECT a FROM AffectedGenomicModel a"
				+ " LEFT JOIN FETCH a.agmFullName"
				+ " LEFT JOIN FETCH a.subtype"
				+ " LEFT JOIN FETCH a.dataProvider"
				+ " LEFT JOIN FETCH a.dataProviderCrossReference"
				+ " WHERE a.id IN :ids",
				AffectedGenomicModel.class)
			.setParameter("ids", ids)
			.getResultList();
	}

	private static final String DO_AGR_SLIM_SUBSET = "DO_AGR_slim";

	/**
	 * SCRUM-5124 - hydrates {@link ModelSearchResultDocument}s for the given AGM ids using a
	 * fixed number of bulk native queries (one root + one per *ToMany layer), independent of
	 * page size. Replaces the per-AGM Hibernate.initialize() walker in ModelDocumentController.
	 */
	@SuppressWarnings("unchecked")
	public SearchResponse<ModelSearchResultDocument> findAgmsForSummaryByIds(List<Long> agmIds) {
		if (CollectionUtils.isEmpty(agmIds)) {
			SearchResponse<ModelSearchResultDocument> empty = new SearchResponse<>();
			empty.setTotalResults(0L);
			return empty;
		}

		// Step 1: AGM root + *ToOne hops + agmFullName + species + xref + RDP in one query.
		// LinkedHashMap preserves the order of the input id list, which matches how the caller
		// chunks pages from getAllIds() and expects results back in the same order.
		Map<Long, ModelSearchResultDocument> docMap = new LinkedHashMap<>();
		Map<Long, String> speciesAbbrevById = new HashMap<>();

		String rootSql = """
			SELECT a.id,
				be.primaryexternalid,
				be.modinternalid,
				sa_full.formattext  AS full_format,
				sa_full.displaytext AS full_display,
				sp.fullname         AS species_full,
				sp.abbreviation     AS species_abbrev,
				cr.referencedcurie  AS xref_curie,
				rd.urltemplate      AS rdp_url_template
			FROM affectedgenomicmodel a
			INNER JOIN biologicalentity be ON be.id = a.id
			LEFT JOIN slotannotation sa_full
				ON sa_full.singleagm_id = a.id
				AND sa_full.slotannotationtype = 'AgmFullNameSlotAnnotation'
			LEFT JOIN ontologyterm ot ON ot.id = be.taxon_id
			LEFT JOIN species sp ON sp.taxon_id = ot.id
			LEFT JOIN crossreference cr ON cr.id = be.dataprovidercrossreference_id
			LEFT JOIN resourcedescriptorpage rd ON rd.id = cr.resourcedescriptorpage_id
			WHERE a.id IN :ids
			""";

		Query rootQuery = entityManager.createNativeQuery(rootSql);
		rootQuery.setParameter("ids", agmIds);
		List<Object[]> rootRows = rootQuery.getResultList();

		for (Object[] row : rootRows) {
			Long id = (Long) row[0];
			String primaryExtId = (String) row[1];
			String modInternalId = (String) row[2];
			String fullFormat = (String) row[3];
			String fullDisplay = (String) row[4];
			String speciesFull = (String) row[5];
			String speciesAbbrev = (String) row[6];
			String xrefCurie = (String) row[7];
			String urlTemplate = (String) row[8];

			ModelSearchResultDocument doc = new ModelSearchResultDocument();
			doc.setPrimaryKey(primaryExtId);
			doc.setGlobalId(primaryExtId);
			doc.setLocalId(modInternalId);
			doc.setName(fullDisplay);
			doc.setNameText(fullFormat);
			doc.setNameKey(composeWithSpecies(fullFormat, speciesAbbrev));
			doc.setSpecies(speciesFull);
			if (urlTemplate != null && xrefCurie != null) {
				doc.setModCrossRefCompleteUrl(urlTemplate.replace("[%s]", xrefCurie));
			}

			docMap.put(id, doc);
			if (speciesAbbrev != null) {
				speciesAbbrevById.put(id, speciesAbbrev);
			}
		}

		// Re-order via the input id sequence so callers see results in the order they asked for.
		Map<Long, ModelSearchResultDocument> ordered = new LinkedHashMap<>();
		for (Long id : agmIds) {
			ModelSearchResultDocument doc = docMap.get(id);
			if (doc != null) {
				ordered.put(id, doc);
			}
		}

		runEnrichment("enrichWithSynonyms", agmIds, () -> enrichWithSynonyms(ordered, agmIds));
		runEnrichment("enrichWithSecondaryIds", agmIds, () -> enrichWithSecondaryIds(ordered, agmIds));
		runEnrichment("enrichWithPhenotypes", agmIds, () -> enrichWithPhenotypes(ordered, agmIds));
		runEnrichment("enrichWithDiseases", agmIds, () -> enrichWithDiseases(ordered, agmIds));
		runEnrichment("enrichWithComponents", agmIds, () -> enrichWithComponents(ordered, agmIds));

		SearchResponse<ModelSearchResultDocument> response = new SearchResponse<>();
		response.setResults(new ArrayList<>(ordered.values()));
		response.setTotalResults((long) ordered.size());
		return response;
	}

	@SuppressWarnings("unchecked")
	private void enrichWithSynonyms(Map<Long, ModelSearchResultDocument> docMap, List<Long> agmIds) {
		String sql = """
			SELECT singleagm_id, displaytext
			FROM slotannotation
			WHERE slotannotationtype = 'AgmSynonymSlotAnnotation'
			AND singleagm_id IN :ids
			""";
		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("ids", agmIds);
		for (Object[] row : (List<Object[]>) q.getResultList()) {
			Long agmId = (Long) row[0];
			String synonym = (String) row[1];
			ModelSearchResultDocument doc = docMap.get(agmId);
			if (doc != null && synonym != null) {
				doc.getSynonyms().add(synonym);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void enrichWithSecondaryIds(Map<Long, ModelSearchResultDocument> docMap, List<Long> agmIds) {
		String sql = """
			SELECT singleagm_id, secondaryid
			FROM slotannotation
			WHERE slotannotationtype = 'AgmSecondaryIdSlotAnnotation'
			AND singleagm_id IN :ids
			""";
		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("ids", agmIds);
		for (Object[] row : (List<Object[]>) q.getResultList()) {
			Long agmId = (Long) row[0];
			String sid = (String) row[1];
			ModelSearchResultDocument doc = docMap.get(agmId);
			if (doc != null && sid != null) {
				doc.getSecondaryIds().add(sid);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void enrichWithPhenotypes(Map<Long, ModelSearchResultDocument> docMap, List<Long> agmIds) {
		// agmphenotypeannotation only holds AGM-specific columns; phenotypeannotationobject
		// lives on the parent phenotypeannotation table (joined inheritance).
		String sql = """
			SELECT a.phenotypeannotationsubject_id, p.phenotypeannotationobject
			FROM agmphenotypeannotation a
			JOIN phenotypeannotation p ON a.id = p.id
			WHERE a.phenotypeannotationsubject_id IN :ids
			""";
		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("ids", agmIds);
		for (Object[] row : (List<Object[]>) q.getResultList()) {
			Long agmId = (Long) row[0];
			String statement = (String) row[1];
			ModelSearchResultDocument doc = docMap.get(agmId);
			if (doc != null && statement != null) {
				doc.getPhenotypeStatements().add(statement);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void enrichWithDiseases(Map<Long, ModelSearchResultDocument> docMap, List<Long> agmIds) {
		// agmdiseaseannotation only holds AGM-specific columns; diseaseannotationobject_id
		// lives on the parent diseaseannotation table (joined inheritance).
		String directSql = """
			SELECT da.diseaseannotationsubject_id,
				ot.id,
				ot.name,
				ot.curie,
				EXISTS(SELECT 1 FROM ontologyterm_subsets s
					WHERE s.ontologyterm_id = ot.id AND s.subsets = :slim) AS in_slim
			FROM agmdiseaseannotation da
			INNER JOIN diseaseannotation parent ON parent.id = da.id
			INNER JOIN ontologyterm ot ON ot.id = parent.diseaseannotationobject_id
			WHERE da.diseaseannotationsubject_id IN :ids
			""";
		Query direct = entityManager.createNativeQuery(directSql);
		direct.setParameter("ids", agmIds);
		direct.setParameter("slim", DO_AGR_SLIM_SUBSET);

		Map<Long, Set<Long>> agmToDiseaseIds = new HashMap<>();
		for (Object[] row : (List<Object[]>) direct.getResultList()) {
			Long agmId = (Long) row[0];
			Long diseaseTermId = (Long) row[1];
			String name = (String) row[2];
			String curie = (String) row[3];
			Boolean inSlim = (Boolean) row[4];

			ModelSearchResultDocument doc = docMap.get(agmId);
			if (doc == null) {
				continue;
			}
			String key = displayKey(name, curie);
			if (key == null) {
				continue;
			}
			doc.getDiseases().add(key);
			doc.getDiseasesWithParents().add(key);
			if (Boolean.TRUE.equals(inSlim)) {
				doc.getDiseasesAgrSlim().add(key);
			}
			if (diseaseTermId != null) {
				agmToDiseaseIds.computeIfAbsent(agmId, k -> new HashSet<>()).add(diseaseTermId);
			}
		}

		if (agmToDiseaseIds.isEmpty()) {
			return;
		}

		Set<Long> allDiseaseIds = agmToDiseaseIds.values().stream()
			.flatMap(Set::stream).collect(Collectors.toSet());

		String ancestorSql = """
			SELECT c.closuresubject_id,
				anc.name,
				anc.curie,
				EXISTS(SELECT 1 FROM ontologyterm_subsets s
					WHERE s.ontologyterm_id = anc.id AND s.subsets = :slim) AS in_slim
			FROM ontologytermclosure c
			INNER JOIN ontologyterm anc ON anc.id = c.closureobject_id
			WHERE c.closuresubject_id IN :ids
			""";
		Query ancestors = entityManager.createNativeQuery(ancestorSql);
		ancestors.setParameter("ids", allDiseaseIds);
		ancestors.setParameter("slim", DO_AGR_SLIM_SUBSET);

		Map<Long, List<Object[]>> ancestorsByDisease = new HashMap<>();
		for (Object[] row : (List<Object[]>) ancestors.getResultList()) {
			Long diseaseId = (Long) row[0];
			ancestorsByDisease.computeIfAbsent(diseaseId, k -> new ArrayList<>()).add(row);
		}

		for (Map.Entry<Long, Set<Long>> entry : agmToDiseaseIds.entrySet()) {
			ModelSearchResultDocument doc = docMap.get(entry.getKey());
			if (doc == null) {
				continue;
			}
			for (Long diseaseId : entry.getValue()) {
				List<Object[]> rows = ancestorsByDisease.get(diseaseId);
				if (rows == null) {
					continue;
				}
				for (Object[] row : rows) {
					String key = displayKey((String) row[1], (String) row[2]);
					if (key == null) {
						continue;
					}
					doc.getDiseasesWithParents().add(key);
					if (Boolean.TRUE.equals(row[3])) {
						doc.getDiseasesAgrSlim().add(key);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void enrichWithComponents(Map<Long, ModelSearchResultDocument> docMap, List<Long> agmIds) {
		String alleleSql = """
			SELECT comp.agmassociationsubject_id,
				allele.id,
				sa_allele.formattext,
				sp_a.abbreviation
			FROM agmalleleassociation comp
			INNER JOIN allele allele ON allele.id = comp.agmalleleassociationobject_id
			INNER JOIN biologicalentity be_a ON be_a.id = allele.id
			LEFT JOIN slotannotation sa_allele
				ON sa_allele.singleallele_id = allele.id
				AND sa_allele.slotannotationtype = 'AlleleSymbolSlotAnnotation'
			LEFT JOIN ontologyterm ot_a ON ot_a.id = be_a.taxon_id
			LEFT JOIN species sp_a ON sp_a.taxon_id = ot_a.id
			WHERE comp.agmassociationsubject_id IN :ids
			""";
		Query q = entityManager.createNativeQuery(alleleSql);
		q.setParameter("ids", agmIds);

		Map<Long, Set<Long>> agmToAlleleIds = new HashMap<>();
		for (Object[] row : (List<Object[]>) q.getResultList()) {
			Long agmId = (Long) row[0];
			Long alleleId = (Long) row[1];
			String alleleFormat = (String) row[2];
			String alleleSpecies = (String) row[3];

			ModelSearchResultDocument doc = docMap.get(agmId);
			if (doc == null) {
				continue;
			}
			if (alleleFormat != null) {
				doc.getAlleles().add(composeWithSpecies(alleleFormat, alleleSpecies));
			}
			if (alleleId != null) {
				agmToAlleleIds.computeIfAbsent(agmId, k -> new HashSet<>()).add(alleleId);
			}
		}

		if (agmToAlleleIds.isEmpty()) {
			return;
		}

		Set<Long> allAlleleIds = agmToAlleleIds.values().stream()
			.flatMap(Set::stream).collect(Collectors.toSet());

		String geneSql = """
			SELECT aga.alleleassociationsubject_id,
				sa_gene.formattext,
				sp_g.abbreviation
			FROM allelegeneassociation aga
			INNER JOIN gene g ON g.id = aga.allelegeneassociationobject_id
			INNER JOIN biologicalentity be_g ON be_g.id = g.id
			LEFT JOIN slotannotation sa_gene
				ON sa_gene.singlegene_id = g.id
				AND sa_gene.slotannotationtype = 'GeneSymbolSlotAnnotation'
			LEFT JOIN ontologyterm ot_g ON ot_g.id = be_g.taxon_id
			LEFT JOIN species sp_g ON sp_g.taxon_id = ot_g.id
			WHERE aga.alleleassociationsubject_id IN :ids
			""";

		// PostgreSQL caps prepared statements at 65,535 parameters; a 1,500-AGM batch
		// can blow past that once allele components are flattened. Chunk to stay safe.
		Map<Long, List<String>> genesByAllele = new HashMap<>();
		List<Long> alleleIdList = new ArrayList<>(allAlleleIds);
		final int inClauseChunk = 10_000;
		for (int from = 0; from < alleleIdList.size(); from += inClauseChunk) {
			List<Long> chunk = alleleIdList.subList(from, Math.min(from + inClauseChunk, alleleIdList.size()));
			Query g = entityManager.createNativeQuery(geneSql);
			g.setParameter("ids", chunk);
			for (Object[] row : (List<Object[]>) g.getResultList()) {
				Long alleleId = (Long) row[0];
				String geneFormat = (String) row[1];
				String geneSpecies = (String) row[2];
				if (geneFormat == null) {
					continue;
				}
				genesByAllele.computeIfAbsent(alleleId, k -> new ArrayList<>())
					.add(composeWithSpecies(geneFormat, geneSpecies));
			}
		}

		for (Map.Entry<Long, Set<Long>> entry : agmToAlleleIds.entrySet()) {
			ModelSearchResultDocument doc = docMap.get(entry.getKey());
			if (doc == null) {
				continue;
			}
			for (Long alleleId : entry.getValue()) {
				List<String> genes = genesByAllele.get(alleleId);
				if (genes != null) {
					doc.getGenes().addAll(genes);
				}
			}
		}
	}

	/**
	 * Wraps an enrichment step so a sporadic SQL/data failure pinpoints itself: the log
	 * line includes the failing step name, the batch size, and the first + last AGM IDs.
	 * That gives the indexer log a needle even when only a single batch out of hundreds
	 * blows up. Re-throws to preserve the existing error-propagation contract.
	 */
	private static void runEnrichment(String step, List<Long> agmIds, Runnable body) {
		try {
			body.run();
		} catch (RuntimeException e) {
			Long first = agmIds.isEmpty() ? null : agmIds.get(0);
			Long last = agmIds.isEmpty() ? null : agmIds.get(agmIds.size() - 1);
			log.error("[findAgmsForSummaryByIds] step={} failed; batchSize={} firstId={} lastId={}",
					step, agmIds.size(), first, last, e);
			throw e;
		}
	}

	private static String composeWithSpecies(String text, String speciesAbbrev) {
		if (text == null) {
			return null;
		}
		return speciesAbbrev != null ? text + " (" + speciesAbbrev + ")" : text;
	}

	private static String displayKey(String name, String curie) {
		if (name != null) {
			return name;
		}
		return curie;
	}

}
