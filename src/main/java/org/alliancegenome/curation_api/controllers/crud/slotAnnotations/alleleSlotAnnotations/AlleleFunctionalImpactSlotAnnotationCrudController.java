package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleFunctionalImpactSlotAnnotationCrudController extends
	BaseEntityCrudController<AlleleFunctionalImpactSlotAnnotationService, AlleleFunctionalImpactSlotAnnotation, AlleleFunctionalImpactSlotAnnotationDAO> implements AlleleFunctionalImpactSlotAnnotationCrudInterface {

	@Inject
	AlleleFunctionalImpactSlotAnnotationService alleleFunctionalImpactService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleFunctionalImpactService);
	}

	@Override
	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> update(AlleleFunctionalImpactSlotAnnotation entity) {
		return alleleFunctionalImpactService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> create(AlleleFunctionalImpactSlotAnnotation entity) {
		return alleleFunctionalImpactService.upsert(entity);
	}

	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> validate(AlleleFunctionalImpactSlotAnnotation entity) {
		return alleleFunctionalImpactService.validate(entity);
	}
}
