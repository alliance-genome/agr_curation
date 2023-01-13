package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationService;

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
