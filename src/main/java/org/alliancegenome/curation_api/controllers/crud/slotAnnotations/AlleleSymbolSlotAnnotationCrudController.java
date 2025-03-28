package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AlleleSymbolSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AlleleSymbolSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleSymbolSlotAnnotationCrudController extends
		BaseEntityCrudController<AlleleSymbolSlotAnnotationService, AlleleSymbolSlotAnnotation, AlleleSymbolSlotAnnotationDAO>
		implements AlleleSymbolSlotAnnotationCrudInterface {

	@Inject
	AlleleSymbolSlotAnnotationService alleleSymbolService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleSymbolService);
	}

	@Override
	public ObjectResponse<AlleleSymbolSlotAnnotation> update(AlleleSymbolSlotAnnotation entity) {
		return alleleSymbolService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleSymbolSlotAnnotation> create(AlleleSymbolSlotAnnotation entity) {
		return alleleSymbolService.upsert(entity);
	}

	public ObjectResponse<AlleleSymbolSlotAnnotation> validate(AlleleSymbolSlotAnnotation entity) {
		return alleleSymbolService.validate(entity);
	}
}
