package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AGMPhenotypeAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AGMPhenotypeAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMPhenotypeAnnotationCrudController extends BaseEntityCrudController<AGMPhenotypeAnnotationService, AGMPhenotypeAnnotation, AGMPhenotypeAnnotationDAO> implements AGMPhenotypeAnnotationCrudInterface {

	@Inject
	AGMPhenotypeAnnotationService agmPhenotypeAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmPhenotypeAnnotationService);
	}
	
	public ObjectResponse<AGMPhenotypeAnnotation> getByIdentifier(String identifierString) {
		return agmPhenotypeAnnotationService.getByIdentifier(identifierString);
	}
}
