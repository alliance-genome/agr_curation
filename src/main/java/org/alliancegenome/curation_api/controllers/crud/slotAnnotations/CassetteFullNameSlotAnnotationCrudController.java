package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.CassetteFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.CassetteFullNameSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteFullNameSlotAnnotationCrudController extends
	BaseEntityCrudController<CassetteFullNameSlotAnnotationService, CassetteFullNameSlotAnnotation, CassetteFullNameSlotAnnotationDAO> implements CassetteFullNameSlotAnnotationCrudInterface {

	@Inject
	CassetteFullNameSlotAnnotationService cassetteFullNameService;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteFullNameService);
	}

	@Override
	public ObjectResponse<CassetteFullNameSlotAnnotation> update(CassetteFullNameSlotAnnotation entity) {
		return cassetteFullNameService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteFullNameSlotAnnotation> create(CassetteFullNameSlotAnnotation entity) {
		return cassetteFullNameService.upsert(entity);
	}

	public ObjectResponse<CassetteFullNameSlotAnnotation> validate(CassetteFullNameSlotAnnotation entity) {
		return cassetteFullNameService.validate(entity);
	}
}
