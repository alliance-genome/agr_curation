package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	GeneToGeneOrthologyDAO geneToGeneOrthologyDAO;
	@Inject
	GeneInteractionDAO geneInteractionDAO;
	@Inject
	AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject
	AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject
	GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject
	GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;

	protected GeneDAO() {
		super(Gene.class);
	}

	public Boolean hasReferencingDiseaseAnnotations(Long geneId) {
		Map<String, Object> geneDaParams = new HashMap<>();
		geneDaParams.put("query_operator", "or");
		geneDaParams.put("diseaseAnnotationSubject.id", geneId);
		geneDaParams.put("diseaseGeneticModifiers.id", geneId);
		geneDaParams.put("with.id", geneId);
		List<Long> results = geneDiseaseAnnotationDAO.findIdsByParams(geneDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> alleleDaParams = new HashMap<>();
		alleleDaParams.put("query_operator", "or");
		alleleDaParams.put("assertedGenes.id", geneId);
		alleleDaParams.put("inferredGene.id", geneId);
		alleleDaParams.put("diseaseGeneticModifiers.id", geneId);
		alleleDaParams.put("with.id", geneId);
		results = alleleDiseaseAnnotationDAO.findIdsByParams(alleleDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmDaParams = new HashMap<>();
		agmDaParams.put("query_operator", "or");
		agmDaParams.put("assertedGenes.id", geneId);
		agmDaParams.put("inferredGene.id", geneId);
		agmDaParams.put("diseaseGeneticModifiers.id", geneId);
		agmDaParams.put("with.id", geneId);
		results = agmDiseaseAnnotationDAO.findIdsByParams(agmDaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingInteractions(Long geneId) {
		Map<String, Object> interactionParams = new HashMap<>();
		interactionParams.put("query_operator", "or");
		interactionParams.put("geneAssociationSubject.id", geneId);
		interactionParams.put("geneGeneAssociationObject.id", geneId);
		List<Long> results = geneInteractionDAO.findIdsByParams(interactionParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingOrthologyPairs(Long geneId) {
		Map<String, Object> orthologyParams = new HashMap<>();
		orthologyParams.put("query_operator", "or");
		orthologyParams.put("subjectGene.id", geneId);
		orthologyParams.put("objectGene.id", geneId);
		List<Long> results = geneToGeneOrthologyDAO.findIdsByParams(orthologyParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long geneId) {
		Map<String, Object> genePaParams = new HashMap<>();
		genePaParams.put("phenotypeAnnotationSubject.id", geneId);
		List<Long> results = genePhenotypeAnnotationDAO.findIdsByParams(genePaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmPaParams = new HashMap<>();
		agmPaParams.put("query_operator", "or");
		agmPaParams.put("assertedGenes.id", geneId);
		agmPaParams.put("inferredGene.id", geneId);
		results.addAll(agmPhenotypeAnnotationDAO.findIdsByParams(agmPaParams));
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> allelePaParams = new HashMap<>();
		allelePaParams.put("query_operator", "or");
		allelePaParams.put("assertedGenes.id", geneId);
		allelePaParams.put("inferredGene.id", geneId);
		results = allelePhenotypeAnnotationDAO.findIdsByParams(allelePaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public List<Long> findReferencingGeneExpressionAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put("expressionAnnotationSubject.id", geneId);
		return geneExpressionAnnotationDAO.findIdsByParams(params);
	}

	public long getEntityIdFromModID(String id) {
		Query q = entityManager.createNativeQuery("SELECT a.id, a.modEntityId, a.modInternalId FROM biologicalentity a WHERE a.modEntityId = :id");
		q.setParameter("id", id);
		Object[] author = (Object[]) q.getSingleResult();
		return (long) author[0];
	}

	public Map<String, Long> getGeneIdMap() {
		if (geneIdMap.size() > 0) {
			return geneIdMap;
		}
		Query q = entityManager.createNativeQuery("SELECT a.id, a.modEntityId, a.modInternalId FROM biologicalentity as a where exists (select * from gene as g where g.id = a.id)");
		List<Object[]> ids = q.getResultList();
		ids.forEach(record -> {
			if (record[1] != null) {
				geneIdMap.put((String) record[1], (long)record[0]);
			}
			if (record[2] != null) {
				geneIdMap.put((String) record[2], (long)record[0]);
			}
		});
		return getGeneIdMap();
	}

	private Map<String, Long> geneIdMap = new HashMap<>();

	public long getGeneIdByModID(String modID) {
		return getGeneIdMap().get(modID);
	}
}
