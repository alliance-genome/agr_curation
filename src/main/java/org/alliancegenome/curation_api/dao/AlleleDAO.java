package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDTO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.apache.commons.collections.CollectionUtils;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {

	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject AgmAlleleAssociationDAO agmAlleleAssociationDAO;
	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;

	protected AlleleDAO() {
		super(Allele.class);
	}

	public Boolean hasReferencingDiseaseAnnotations(Long alleleId) {

		Map<String, Object> alleleDaParams = new HashMap<>();
		alleleDaParams.put("query_operator", "or");
		alleleDaParams.put(EntityFieldConstants.DA_SUBJECT + ".id", alleleId);
		alleleDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		List<Long> results = alleleDiseaseAnnotationDAO.findIdsByParams(alleleDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmDaParams = new HashMap<>();
		agmDaParams.put("query_operator", "or");
		agmDaParams.put(EntityFieldConstants.ASSERTED_ALLELES + ".id", alleleId);
		agmDaParams.put(EntityFieldConstants.INFERRED_ALLELE + ".id", alleleId);
		agmDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		results = agmDiseaseAnnotationDAO.findIdsByParams(agmDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> geneDaParams = new HashMap<>();
		geneDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		results = geneDiseaseAnnotationDAO.findIdsByParams(geneDaParams);

		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long alleleId) {

		Map<String, Object> allelePaParams = new HashMap<>();
		allelePaParams.put(EntityFieldConstants.PA_SUBJECT + ".id", alleleId);
		List<Long> results = allelePhenotypeAnnotationDAO.findIdsByParams(allelePaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmPaParams = new HashMap<>();
		agmPaParams.put("query_operator", "or");
		agmPaParams.put(EntityFieldConstants.ASSERTED_ALLELES + ".id", alleleId);
		agmPaParams.put(EntityFieldConstants.INFERRED_ALLELE + ".id", alleleId);
		results = agmPhenotypeAnnotationDAO.findIdsByParams(agmPaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingAgmAlleleAssociations(Long alleleId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ALLELE_ASSOCIATION_OBJECT + ".id", alleleId);
		List<Long> results = agmAlleleAssociationDAO.findIdsByParams(params);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingHTPExpressionDatasetSampleAnnotation(Long alleleId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.GENOMIC_INFORMATION_ALLELE + ".id", alleleId);
		List<Long> results = htpExpressionDatasetSampleAnnotationDAO.findIdsByParams(params);

		return CollectionUtils.isNotEmpty(results);
	}

	public List<String> getAllAllelePrimaryExternalIds() {
		String sql = """
			SELECT be.primaryexternalid
			FROM biologicalentity be, allele as a
			WHERE be.id = a.id and be.primaryexternalid is not NULL
		""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();

		objects.forEach(object -> {
			list.add((String) object);
		});

		return list;
	}
	public SearchResponse<AlleleSummaryDTO> findAllelesForSummary(Pagination pagination, Map<String, Object> params) {
		SearchResponse<Allele> filteredResults = super.findByParams(pagination, params, null);

		if (filteredResults.getResults().isEmpty()) {
			SearchResponse<AlleleSummaryDTO> emptyResponse = new SearchResponse<>();
			emptyResponse.setResults(new ArrayList<>());
			emptyResponse.setTotalResults(filteredResults.getTotalResults());
			return emptyResponse;
		}

		List<Long> alleleIds = filteredResults.getResults().stream()
			.map(Allele::getId)
			.collect(java.util.stream.Collectors.toList());

		String baseQuery = """
			SELECT DISTINCT a FROM Allele a
			LEFT JOIN FETCH a.dataProvider
			LEFT JOIN FETCH a.dataProviderCrossReference
			WHERE a.id IN :alleleIds
			ORDER BY a.id
			""";

		TypedQuery<Allele> query = entityManager.createQuery(baseQuery, Allele.class);
		query.setParameter("alleleIds", alleleIds);
		List<Allele> alleles = query.getResultList();

		Map<Long, Long> variantCountMap = new HashMap<>();

		if (!alleleIds.isEmpty()) {
			String geneAssocQuery = """
				SELECT DISTINCT a FROM Allele a
				LEFT JOIN FETCH a.alleleGeneAssociations aga
				LEFT JOIN FETCH aga.alleleGeneAssociationObject gene
				LEFT JOIN FETCH gene.geneSymbol
				LEFT JOIN FETCH aga.relation r
				WHERE a.id IN :alleleIds
				AND r.name = 'is_allele_of'
				AND aga.internal = false
				AND aga.obsolete = false
				and a.alleleGeneAssociations is not empty
				""";
			TypedQuery<Allele> geneQuery = entityManager.createQuery(geneAssocQuery, Allele.class);
			geneQuery.setParameter("alleleIds", alleleIds);
			geneQuery.getResultList(); // This loads the associations into the session

			String constructAssocQuery = """
				SELECT DISTINCT a FROM Allele a
				LEFT JOIN FETCH a.alleleConstructAssociations aca
				LEFT JOIN FETCH aca.alleleConstructAssociationObject construct
				LEFT JOIN FETCH construct.constructSymbol
				WHERE a.id IN :alleleIds and a.alleleConstructAssociations is not empty
				""";
			TypedQuery<Allele> constructQuery = entityManager.createQuery(constructAssocQuery, Allele.class);
			constructQuery.setParameter("alleleIds", alleleIds);
			constructQuery.getResultList();

			String variantCountQuery = """
				SELECT a.id, COUNT(ava.id)
				FROM Allele a
				LEFT JOIN a.alleleVariantAssociations ava
				WHERE a.id IN :alleleIds and a.alleleVariantAssociations is not empty
				GROUP BY a.id
				""";
			Query variantCountQueryExec = entityManager.createQuery(variantCountQuery);
			variantCountQueryExec.setParameter("alleleIds", alleleIds);
			List<Object[]> variantCounts = variantCountQueryExec.getResultList();

			for (Object[] row : variantCounts) {
				variantCountMap.put((Long) row[0], (Long) row[1]);
			}

			String notesQuery = """
				SELECT DISTINCT a FROM Allele a
				LEFT JOIN FETCH a.relatedNotes
				WHERE a.id IN :alleleIds and a.relatedNotes is not empty
				""";
			TypedQuery<Allele> notesQueryExec = entityManager.createQuery(notesQuery, Allele.class);
			notesQueryExec.setParameter("alleleIds", alleleIds);
			notesQueryExec.getResultList();
		}

		List<AlleleSummaryDTO> dtos = new ArrayList<>();
		for (Allele allele : alleles) {
			Long count = variantCountMap.getOrDefault(allele.getId(), 0L);
			dtos.add(new AlleleSummaryDTO(allele, count));
		}

		SearchResponse<AlleleSummaryDTO> response = new SearchResponse<>();
		response.setResults(dtos);
		response.setTotalResults(filteredResults.getTotalResults());

		return response;
	}


}
