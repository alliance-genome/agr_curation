package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.CassetteComponentSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.CassetteComponentSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteComponentSlotAnnotationCrudController extends
	BaseEntityCrudController<CassetteComponentSlotAnnotationService, CassetteComponentSlotAnnotation, CassetteComponentSlotAnnotationDAO> implements CassetteComponentSlotAnnotationCrudInterface {

	@Inject
	CassetteComponentSlotAnnotationService cassetteComponentService;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteComponentService);
	}

	@Override
	public ObjectResponse<CassetteComponentSlotAnnotation> update(CassetteComponentSlotAnnotation entity) {
		return cassetteComponentService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteComponentSlotAnnotation> create(CassetteComponentSlotAnnotation entity) {
		return cassetteComponentService.upsert(entity);
	}

	public ObjectResponse<CassetteComponentSlotAnnotation> validate(CassetteComponentSlotAnnotation entity) {
		return cassetteComponentService.validate(entity);
	}
}
