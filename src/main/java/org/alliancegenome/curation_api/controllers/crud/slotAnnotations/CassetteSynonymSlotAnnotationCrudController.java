package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.CassetteSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.CassetteSynonymSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteSynonymSlotAnnotationCrudController extends
	BaseEntityCrudController<CassetteSynonymSlotAnnotationService, CassetteSynonymSlotAnnotation, CassetteSynonymSlotAnnotationDAO> implements CassetteSynonymSlotAnnotationCrudInterface {

	@Inject
	CassetteSynonymSlotAnnotationService cassetteSynonymService;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteSynonymService);
	}

	@Override
	public ObjectResponse<CassetteSynonymSlotAnnotation> update(CassetteSynonymSlotAnnotation entity) {
		return cassetteSynonymService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteSynonymSlotAnnotation> create(CassetteSynonymSlotAnnotation entity) {
		return cassetteSynonymService.upsert(entity);
	}

	public ObjectResponse<CassetteSynonymSlotAnnotation> validate(CassetteSynonymSlotAnnotation entity) {
		return cassetteSynonymService.validate(entity);
	}
}
