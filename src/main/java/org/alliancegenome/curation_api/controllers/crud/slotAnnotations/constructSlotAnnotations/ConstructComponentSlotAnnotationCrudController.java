package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructComponentSlotAnnotationCrudController extends
	BaseEntityCrudController<ConstructComponentSlotAnnotationService, ConstructComponentSlotAnnotation, ConstructComponentSlotAnnotationDAO> implements ConstructComponentSlotAnnotationCrudInterface {

	@Inject
	ConstructComponentSlotAnnotationService constructComponentService;

	@Override
	@PostConstruct
	protected void init() {
		setService(constructComponentService);
	}

	@Override
	public ObjectResponse<ConstructComponentSlotAnnotation> update(ConstructComponentSlotAnnotation entity) {
		return constructComponentService.upsert(entity);
	}

	@Override
	public ObjectResponse<ConstructComponentSlotAnnotation> create(ConstructComponentSlotAnnotation entity) {
		return constructComponentService.upsert(entity);
	}

	public ObjectResponse<ConstructComponentSlotAnnotation> validate(ConstructComponentSlotAnnotation entity) {
		return constructComponentService.validate(entity);
	}
}
