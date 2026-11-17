package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.TransgenicToolSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.TransgenicToolSynonymSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolSynonymSlotAnnotationCrudController extends
	BaseEntityCrudController<TransgenicToolSynonymSlotAnnotationService, TransgenicToolSynonymSlotAnnotation, TransgenicToolSynonymSlotAnnotationDAO> implements TransgenicToolSynonymSlotAnnotationCrudInterface {

	@Inject
	TransgenicToolSynonymSlotAnnotationService transgenicToolSynonymService;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolSynonymService);
	}

	@Override
	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> update(TransgenicToolSynonymSlotAnnotation entity) {
		return transgenicToolSynonymService.upsert(entity);
	}

	@Override
	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> create(TransgenicToolSynonymSlotAnnotation entity) {
		return transgenicToolSynonymService.upsert(entity);
	}

	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> validate(TransgenicToolSynonymSlotAnnotation entity) {
		return transgenicToolSynonymService.validate(entity);
	}
}
