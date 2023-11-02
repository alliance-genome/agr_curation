package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructSynonymSlotAnnotationCrudController extends
	BaseEntityCrudController<ConstructSynonymSlotAnnotationService, ConstructSynonymSlotAnnotation, ConstructSynonymSlotAnnotationDAO> implements ConstructSynonymSlotAnnotationCrudInterface {

	@Inject
	ConstructSynonymSlotAnnotationService constructSynonymService;

	@Override
	@PostConstruct
	protected void init() {
		setService(constructSynonymService);
	}

	@Override
	public ObjectResponse<ConstructSynonymSlotAnnotation> update(ConstructSynonymSlotAnnotation entity) {
		return constructSynonymService.upsert(entity);
	}

	@Override
	public ObjectResponse<ConstructSynonymSlotAnnotation> create(ConstructSynonymSlotAnnotation entity) {
		return constructSynonymService.upsert(entity);
	}

	public ObjectResponse<ConstructSynonymSlotAnnotation> validate(ConstructSynonymSlotAnnotation entity) {
		return constructSynonymService.validate(entity);
	}
}
