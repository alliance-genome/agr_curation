package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GenePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GenePhenotypeAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GenePhenotypeAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenePhenotypeAnnotationCrudController extends BaseEntityCrudController<GenePhenotypeAnnotationService, GenePhenotypeAnnotation, GenePhenotypeAnnotationDAO> implements GenePhenotypeAnnotationCrudInterface {

	@Inject
	GenePhenotypeAnnotationService genePhenotypeAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(genePhenotypeAnnotationService);
	}
	
	public ObjectResponse<GenePhenotypeAnnotation> get(String identifierString) {
		return genePhenotypeAnnotationService.get(identifierString);
	}
}
