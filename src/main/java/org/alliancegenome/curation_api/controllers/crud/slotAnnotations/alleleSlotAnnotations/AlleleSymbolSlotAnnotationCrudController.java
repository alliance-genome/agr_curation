package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationService;

@RequestScoped
public class AlleleSymbolSlotAnnotationCrudController extends BaseEntityCrudController<AlleleSymbolSlotAnnotationService, AlleleSymbolSlotAnnotation, AlleleSymbolSlotAnnotationDAO> implements AlleleSymbolSlotAnnotationCrudInterface {

	@Inject AlleleSymbolSlotAnnotationService alleleSymbolService;
	
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
