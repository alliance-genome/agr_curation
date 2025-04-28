package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AgmFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AgmFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AgmFullNameSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmFullNameSlotAnnotationCrudController extends
		BaseEntityCrudController<AgmFullNameSlotAnnotationService, AgmFullNameSlotAnnotation, AgmFullNameSlotAnnotationDAO>
		implements AgmFullNameSlotAnnotationCrudInterface {

	@Inject
	AgmFullNameSlotAnnotationService agmFullNameService;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmFullNameService);
	}

	@Override
	public ObjectResponse<AgmFullNameSlotAnnotation> update(AgmFullNameSlotAnnotation entity) {
		return agmFullNameService.upsert(entity);
	}

	@Override
	public ObjectResponse<AgmFullNameSlotAnnotation> create(AgmFullNameSlotAnnotation entity) {
		return agmFullNameService.upsert(entity);
	}

	public ObjectResponse<AgmFullNameSlotAnnotation> validate(AgmFullNameSlotAnnotation entity) {
		return agmFullNameService.validate(entity);
	}
}
