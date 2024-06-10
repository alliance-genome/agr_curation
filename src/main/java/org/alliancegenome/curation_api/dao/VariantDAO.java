package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;

	protected VariantDAO() {
		super(Variant.class);
	}

	public Boolean hasReferencingDiseaseAnnotationIds(Long variantId) {
		Map<String, Object> dgmParams = new HashMap<>();
		dgmParams.put("diseaseGeneticModifiers.id", variantId);
		List<Long> results = diseaseAnnotationDAO.findIdsByParams(dgmParams);
		return CollectionUtils.isNotEmpty(results);
	}
}
