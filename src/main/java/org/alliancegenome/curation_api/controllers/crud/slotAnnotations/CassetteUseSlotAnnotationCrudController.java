package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.CassetteUseSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.CassetteUseSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteUseSlotAnnotationCrudController extends
	BaseEntityCrudController<CassetteUseSlotAnnotationService, CassetteUseSlotAnnotation, CassetteUseSlotAnnotationDAO> implements CassetteUseSlotAnnotationCrudInterface {

	@Inject
	CassetteUseSlotAnnotationService cassetteUseService;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteUseService);
	}

	@Override
	public ObjectResponse<CassetteUseSlotAnnotation> update(CassetteUseSlotAnnotation entity) {
		return cassetteUseService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteUseSlotAnnotation> create(CassetteUseSlotAnnotation entity) {
		return cassetteUseService.upsert(entity);
	}

	public ObjectResponse<CassetteUseSlotAnnotation> validate(CassetteUseSlotAnnotation entity) {
		return cassetteUseService.validate(entity);
	}
}
