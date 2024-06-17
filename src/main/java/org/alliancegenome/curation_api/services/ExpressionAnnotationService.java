package org.alliancegenome.curation_api.services;

import jakarta.inject.Inject;
import org.alliancegenome.curation_api.dao.ExpressionAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.ExpressionAnnotation;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;

public class ExpressionAnnotationService extends BaseAnnotationCrudService<ExpressionAnnotation, ExpressionAnnotationDAO> {
	@Inject ExpressionAnnotationDAO expressionAnnotationDAO;

	@Override
	protected void init() {
		setSQLDao(expressionAnnotationDAO);
	}
}
