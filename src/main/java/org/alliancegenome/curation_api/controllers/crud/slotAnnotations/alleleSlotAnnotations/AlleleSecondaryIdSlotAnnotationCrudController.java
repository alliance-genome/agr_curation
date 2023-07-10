package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationCrudController extends
	BaseEntityCrudController<AlleleSecondaryIdSlotAnnotationService, AlleleSecondaryIdSlotAnnotation, AlleleSecondaryIdSlotAnnotationDAO> implements AlleleSecondaryIdSlotAnnotationCrudInterface {

	@Inject
	AlleleSecondaryIdSlotAnnotationService alleleSecondaryIdService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleSecondaryIdService);
	}

	@Override
	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> update(AlleleSecondaryIdSlotAnnotation entity) {
		return alleleSecondaryIdService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> create(AlleleSecondaryIdSlotAnnotation entity) {
		return alleleSecondaryIdService.upsert(entity);
	}

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validate(AlleleSecondaryIdSlotAnnotation entity) {
		return alleleSecondaryIdService.validate(entity);
	}
}
