package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AgmSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AgmSecondaryIdSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AgmSecondaryIdSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSecondaryIdSlotAnnotationCrudController extends
		BaseEntityCrudController<AgmSecondaryIdSlotAnnotationService, AgmSecondaryIdSlotAnnotation, AgmSecondaryIdSlotAnnotationDAO>
		implements AgmSecondaryIdSlotAnnotationCrudInterface {

	@Inject
	AgmSecondaryIdSlotAnnotationService agmSecondaryIdService;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmSecondaryIdService);
	}

	@Override
	public ObjectResponse<AgmSecondaryIdSlotAnnotation> update(AgmSecondaryIdSlotAnnotation entity) {
		return agmSecondaryIdService.upsert(entity);
	}

	@Override
	public ObjectResponse<AgmSecondaryIdSlotAnnotation> create(AgmSecondaryIdSlotAnnotation entity) {
		return agmSecondaryIdService.upsert(entity);
	}

	public ObjectResponse<AgmSecondaryIdSlotAnnotation> validate(AgmSecondaryIdSlotAnnotation entity) {
		return agmSecondaryIdService.validate(entity);
	}
}
