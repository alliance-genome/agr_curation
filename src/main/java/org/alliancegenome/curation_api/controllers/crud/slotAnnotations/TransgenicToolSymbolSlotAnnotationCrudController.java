package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.TransgenicToolSymbolSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.TransgenicToolSymbolSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolSymbolSlotAnnotationCrudController extends
	BaseEntityCrudController<TransgenicToolSymbolSlotAnnotationService, TransgenicToolSymbolSlotAnnotation, TransgenicToolSymbolSlotAnnotationDAO> implements TransgenicToolSymbolSlotAnnotationCrudInterface {

	@Inject
	TransgenicToolSymbolSlotAnnotationService transgenicToolSymbolService;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolSymbolService);
	}

	@Override
	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> update(TransgenicToolSymbolSlotAnnotation entity) {
		return transgenicToolSymbolService.upsert(entity);
	}

	@Override
	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> create(TransgenicToolSymbolSlotAnnotation entity) {
		return transgenicToolSymbolService.upsert(entity);
	}

	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> validate(TransgenicToolSymbolSlotAnnotation entity) {
		return transgenicToolSymbolService.validate(entity);
	}
}
