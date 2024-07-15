package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.ExpressionAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.ExpressionAnnotation;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExpressionAnnotationService extends BaseAnnotationCrudService<ExpressionAnnotation, ExpressionAnnotationDAO> {
	
	@Inject ExpressionAnnotationDAO expressionAnnotationDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(expressionAnnotationDAO);
	}
}
