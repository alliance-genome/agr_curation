package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.GeneSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.GeneSynonymSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSynonymSlotAnnotationCrudController extends BaseEntityCrudController<GeneSynonymSlotAnnotationService, GeneSynonymSlotAnnotation, GeneSynonymSlotAnnotationDAO>
	implements GeneSynonymSlotAnnotationCrudInterface {

	@Inject
	GeneSynonymSlotAnnotationService geneSynonymService;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneSynonymService);
	}

	@Override
	public ObjectResponse<GeneSynonymSlotAnnotation> update(GeneSynonymSlotAnnotation entity) {
		return geneSynonymService.upsert(entity);
	}

	@Override
	public ObjectResponse<GeneSynonymSlotAnnotation> create(GeneSynonymSlotAnnotation entity) {
		return geneSynonymService.upsert(entity);
	}

	public ObjectResponse<GeneSynonymSlotAnnotation> validate(GeneSynonymSlotAnnotation entity) {
		return geneSynonymService.validate(entity);
	}
}
