package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.TransgenicToolFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.TransgenicToolFullNameSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolFullNameSlotAnnotationCrudController extends
	BaseEntityCrudController<TransgenicToolFullNameSlotAnnotationService, TransgenicToolFullNameSlotAnnotation, TransgenicToolFullNameSlotAnnotationDAO> implements TransgenicToolFullNameSlotAnnotationCrudInterface {

	@Inject
	TransgenicToolFullNameSlotAnnotationService transgenicToolFullNameService;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolFullNameService);
	}

	@Override
	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> update(TransgenicToolFullNameSlotAnnotation entity) {
		return transgenicToolFullNameService.upsert(entity);
	}

	@Override
	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> create(TransgenicToolFullNameSlotAnnotation entity) {
		return transgenicToolFullNameService.upsert(entity);
	}

	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> validate(TransgenicToolFullNameSlotAnnotation entity) {
		return transgenicToolFullNameService.validate(entity);
	}
}
