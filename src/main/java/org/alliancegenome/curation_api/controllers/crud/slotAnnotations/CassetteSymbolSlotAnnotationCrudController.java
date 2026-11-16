package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.CassetteSymbolSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.CassetteSymbolSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteSymbolSlotAnnotationCrudController extends
	BaseEntityCrudController<CassetteSymbolSlotAnnotationService, CassetteSymbolSlotAnnotation, CassetteSymbolSlotAnnotationDAO> implements CassetteSymbolSlotAnnotationCrudInterface {

	@Inject
	CassetteSymbolSlotAnnotationService cassetteSymbolService;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteSymbolService);
	}

	@Override
	public ObjectResponse<CassetteSymbolSlotAnnotation> update(CassetteSymbolSlotAnnotation entity) {
		return cassetteSymbolService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteSymbolSlotAnnotation> create(CassetteSymbolSlotAnnotation entity) {
		return cassetteSymbolService.upsert(entity);
	}

	public ObjectResponse<CassetteSymbolSlotAnnotation> validate(CassetteSymbolSlotAnnotation entity) {
		return cassetteSymbolService.validate(entity);
	}
}
