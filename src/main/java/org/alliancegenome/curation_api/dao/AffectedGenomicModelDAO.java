package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AffectedGenomicModelDAO extends BaseSQLDAO<AffectedGenomicModel> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	
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
		results = geneDiseaseAnnotationDAO.findIdsByParams(params);
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
		results = genePhenotypeAnnotationDAO.findIdsByParams(params);
		return CollectionUtils.isNotEmpty(results);
	}

}
