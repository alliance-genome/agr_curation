package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.constructSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationService;

@RequestScoped
public class ConstructSymbolSlotAnnotationCrudController extends
	BaseEntityCrudController<ConstructSymbolSlotAnnotationService, ConstructSymbolSlotAnnotation, ConstructSymbolSlotAnnotationDAO> implements ConstructSymbolSlotAnnotationCrudInterface {

	@Inject
	ConstructSymbolSlotAnnotationService constructSymbolService;

	@Override
	@PostConstruct
	protected void init() {
		setService(constructSymbolService);
	}

	@Override
	public ObjectResponse<ConstructSymbolSlotAnnotation> update(ConstructSymbolSlotAnnotation entity) {
		return constructSymbolService.upsert(entity);
	}

	@Override
	public ObjectResponse<ConstructSymbolSlotAnnotation> create(ConstructSymbolSlotAnnotation entity) {
		return constructSymbolService.upsert(entity);
	}

	public ObjectResponse<ConstructSymbolSlotAnnotation> validate(ConstructSymbolSlotAnnotation entity) {
		return constructSymbolService.validate(entity);
	}
}
