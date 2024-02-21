package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {
	
	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	
	protected AlleleDAO() {
		super(Allele.class);
	}

	public List<Long> findReferencingDiseaseAnnotationIds(Long alleleId) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("diseaseAnnotationSubject.id", alleleId);
		List<Long> results = alleleDiseaseAnnotationDAO.findFilteredIds(params);
		
		Map<String, Object> aaParams = new HashMap<>();
		aaParams.put("assertedAllele.id", alleleId);
		results.addAll(agmDiseaseAnnotationDAO.findFilteredIds(aaParams));
		
		Map<String, Object> iaParams = new HashMap<>();
		iaParams.put("inferredAllele.id", alleleId);
		results.addAll(agmDiseaseAnnotationDAO.findFilteredIds(iaParams));
		
		Map<String, Object> dgmParams = new HashMap<>();
		dgmParams.put("diseaseGeneticModifiers.id", alleleId);
		results.addAll(diseaseAnnotationDAO.findFilteredIds(dgmParams));

		return results;
	}

}
