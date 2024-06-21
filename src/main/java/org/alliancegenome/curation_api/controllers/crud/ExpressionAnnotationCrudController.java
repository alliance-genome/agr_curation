package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ExpressionAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.ExpressionAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExpressionAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ExpressionAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExpressionAnnotationCrudController extends BaseEntityCrudController<ExpressionAnnotationService, ExpressionAnnotation, ExpressionAnnotationDAO> implements ExpressionAnnotationCrudInterface {

	@Inject ExpressionAnnotationService expressionAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(expressionAnnotationService);
	}

	public ObjectResponse<ExpressionAnnotation> getByIdentifier(String identifierString) {
		return expressionAnnotationService.getByIdentifier(identifierString);
	}
}

