package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.constructSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationService;

@RequestScoped
public class ConstructFullNameSlotAnnotationCrudController extends
	BaseEntityCrudController<ConstructFullNameSlotAnnotationService, ConstructFullNameSlotAnnotation, ConstructFullNameSlotAnnotationDAO> implements ConstructFullNameSlotAnnotationCrudInterface {

	@Inject
	ConstructFullNameSlotAnnotationService constructFullNameService;

	@Override
	@PostConstruct
	protected void init() {
		setService(constructFullNameService);
	}

	@Override
	public ObjectResponse<ConstructFullNameSlotAnnotation> update(ConstructFullNameSlotAnnotation entity) {
		return constructFullNameService.upsert(entity);
	}

	@Override
	public ObjectResponse<ConstructFullNameSlotAnnotation> create(ConstructFullNameSlotAnnotation entity) {
		return constructFullNameService.upsert(entity);
	}

	public ObjectResponse<ConstructFullNameSlotAnnotation> validate(ConstructFullNameSlotAnnotation entity) {
		return constructFullNameService.validate(entity);
	}
}
