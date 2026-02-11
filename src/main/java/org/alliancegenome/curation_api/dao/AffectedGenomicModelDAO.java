package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

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
		return entityManager.createQuery("SELECT a FROM AffectedGenomicModel a WHERE a.id IN :ids", AffectedGenomicModel.class).setParameter("ids", ids).getResultList();
	}

}
