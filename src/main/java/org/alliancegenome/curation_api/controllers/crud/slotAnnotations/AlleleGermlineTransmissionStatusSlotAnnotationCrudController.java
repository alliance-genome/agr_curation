package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationCrudController extends
		BaseEntityCrudController<AlleleGermlineTransmissionStatusSlotAnnotationService, AlleleGermlineTransmissionStatusSlotAnnotation, AlleleGermlineTransmissionStatusSlotAnnotationDAO>
		implements AlleleGermlineTransmissionStatusSlotAnnotationCrudInterface {

	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationService alleleGermlineTransmissionStatusService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleGermlineTransmissionStatusService);
	}

	@Override
	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> update(
			AlleleGermlineTransmissionStatusSlotAnnotation entity) {
		return alleleGermlineTransmissionStatusService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> create(
			AlleleGermlineTransmissionStatusSlotAnnotation entity) {
		return alleleGermlineTransmissionStatusService.upsert(entity);
	}

	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> validate(
			AlleleGermlineTransmissionStatusSlotAnnotation entity) {
		return alleleGermlineTransmissionStatusService.validate(entity);
	}
}
