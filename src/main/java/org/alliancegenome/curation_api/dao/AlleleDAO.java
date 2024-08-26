package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {

	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;

	protected AlleleDAO() {
		super(Allele.class);
	}

	public Boolean hasReferencingDiseaseAnnotationIds(Long alleleId) {

		Map<String, Object> alleleDaParams = new HashMap<>();
		alleleDaParams.put("query_operator", "or");
		alleleDaParams.put("diseaseAnnotationSubject.id", alleleId);
		alleleDaParams.put("diseaseGeneticModifiers.id", alleleId);
		List<Long> results = alleleDiseaseAnnotationDAO.findIdsByParams(alleleDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmDaParams = new HashMap<>();
		agmDaParams.put("query_operator", "or");
		agmDaParams.put("assertedAllele.id", alleleId);
		agmDaParams.put("inferredAllele.id", alleleId);
		agmDaParams.put("diseaseGeneticModifiers.id", alleleId);
		results = agmDiseaseAnnotationDAO.findIdsByParams(agmDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}
		
		Map<String, Object> geneDaParams = new HashMap<>();
		geneDaParams.put("diseaseGeneticModifiers.id", alleleId);
		results = geneDiseaseAnnotationDAO.findIdsByParams(geneDaParams);

		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long alleleId) {
		
		Map<String, Object> allelePaParams = new HashMap<>();
		allelePaParams.put("phenotypeAnnotationSubject.id", alleleId);
		List<Long> results = allelePhenotypeAnnotationDAO.findIdsByParams(allelePaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmPaParams = new HashMap<>();
		agmPaParams.put("query_operator", "or");
		agmPaParams.put("assertedAllele.id", alleleId);
		agmPaParams.put("inferredAllele.id", alleleId);
		results = agmPhenotypeAnnotationDAO.findIdsByParams(agmPaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Map<String, Long> getAlleleIdMap() {
		if (alleleIdMap.size() > 0) {
			return alleleIdMap;
		}
		Query q = entityManager.createNativeQuery("SELECT a.id, a.modEntityId, a.modInternalId FROM biologicalentity as a where exists (select * from allele as g where g.id = a.id)");
		List<Object[]> ids = q.getResultList();
		ids.forEach(record -> {
			if (record[1] != null) {
				alleleIdMap.put((String) record[1], (long)record[0]);
			}
			if (record[2] != null) {
				alleleIdMap.put((String) record[2], (long)record[0]);
			}
		});
		return alleleIdMap;
	}

	private Map<String, Long> alleleIdMap = new HashMap<>();

	public long getGeneIdByModID(String modID) {
		return getAlleleIdMap().get(modID);
	}

}
