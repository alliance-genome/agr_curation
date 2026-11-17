package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.TransgenicToolUseSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.TransgenicToolUseSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolUseSlotAnnotationCrudController extends
	BaseEntityCrudController<TransgenicToolUseSlotAnnotationService, TransgenicToolUseSlotAnnotation, TransgenicToolUseSlotAnnotationDAO> implements TransgenicToolUseSlotAnnotationCrudInterface {

	@Inject
	TransgenicToolUseSlotAnnotationService transgenicToolUseService;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolUseService);
	}

	@Override
	public ObjectResponse<TransgenicToolUseSlotAnnotation> update(TransgenicToolUseSlotAnnotation entity) {
		return transgenicToolUseService.upsert(entity);
	}

	@Override
	public ObjectResponse<TransgenicToolUseSlotAnnotation> create(TransgenicToolUseSlotAnnotation entity) {
		return transgenicToolUseService.upsert(entity);
	}

	public ObjectResponse<TransgenicToolUseSlotAnnotation> validate(TransgenicToolUseSlotAnnotation entity) {
		return transgenicToolUseService.validate(entity);
	}
}
