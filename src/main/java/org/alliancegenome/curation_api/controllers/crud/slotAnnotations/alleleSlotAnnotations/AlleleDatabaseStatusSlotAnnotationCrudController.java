package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleDatabaseStatusSlotAnnotationCrudController extends
	BaseEntityCrudController<AlleleDatabaseStatusSlotAnnotationService, AlleleDatabaseStatusSlotAnnotation, AlleleDatabaseStatusSlotAnnotationDAO> implements AlleleDatabaseStatusSlotAnnotationCrudInterface {

	@Inject
	AlleleDatabaseStatusSlotAnnotationService alleleDatabaseStatusService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleDatabaseStatusService);
	}

	@Override
	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> update(AlleleDatabaseStatusSlotAnnotation entity) {
		return alleleDatabaseStatusService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> create(AlleleDatabaseStatusSlotAnnotation entity) {
		return alleleDatabaseStatusService.upsert(entity);
	}

	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> validate(AlleleDatabaseStatusSlotAnnotation entity) {
		return alleleDatabaseStatusService.validate(entity);
	}
}
