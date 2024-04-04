package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.AllelePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AllelePhenotypeAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AllelePhenotypeAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AllelePhenotypeAnnotationCrudController extends BaseEntityCrudController<AllelePhenotypeAnnotationService, AllelePhenotypeAnnotation, AllelePhenotypeAnnotationDAO>implements AllelePhenotypeAnnotationCrudInterface {

	@Inject
	AllelePhenotypeAnnotationService allelePhenotypeAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(allelePhenotypeAnnotationService);
	}

	public ObjectResponse<AllelePhenotypeAnnotation> getByIdentifier(String identifierString) {
		return allelePhenotypeAnnotationService.getByIdentifier(identifierString);
	}
}
