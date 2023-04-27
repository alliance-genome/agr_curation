package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleInheritanceModeSlotAnnotationCrudController extends
	BaseEntityCrudController<AlleleInheritanceModeSlotAnnotationService, AlleleInheritanceModeSlotAnnotation, AlleleInheritanceModeSlotAnnotationDAO> implements AlleleInheritanceModeSlotAnnotationCrudInterface {

	@Inject
	AlleleInheritanceModeSlotAnnotationService alleleInheritanceModeService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleInheritanceModeService);
	}

	@Override
	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> update(AlleleInheritanceModeSlotAnnotation entity) {
		return alleleInheritanceModeService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> create(AlleleInheritanceModeSlotAnnotation entity) {
		return alleleInheritanceModeService.upsert(entity);
	}

	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> validate(AlleleInheritanceModeSlotAnnotation entity) {
		return alleleInheritanceModeService.validate(entity);
	}
}
