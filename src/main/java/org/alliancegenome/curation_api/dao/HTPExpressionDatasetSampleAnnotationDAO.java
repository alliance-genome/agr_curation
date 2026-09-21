package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HTPExpressionDatasetSampleAnnotationDAO extends BaseCurieSQLDAO<HTPExpressionDatasetSampleAnnotation> {
	
	protected HTPExpressionDatasetSampleAnnotationDAO() {
		super(HTPExpressionDatasetSampleAnnotation.class);
	}
}
