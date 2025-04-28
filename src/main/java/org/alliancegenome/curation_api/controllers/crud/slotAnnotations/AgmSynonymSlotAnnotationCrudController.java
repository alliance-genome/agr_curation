package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AgmSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AgmSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AgmSynonymSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSynonymSlotAnnotationCrudController extends
		BaseEntityCrudController<AgmSynonymSlotAnnotationService, AgmSynonymSlotAnnotation, AgmSynonymSlotAnnotationDAO>
		implements AgmSynonymSlotAnnotationCrudInterface {

	@Inject
	AgmSynonymSlotAnnotationService agmSynonymService;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmSynonymService);
	}

	@Override
	public ObjectResponse<AgmSynonymSlotAnnotation> update(AgmSynonymSlotAnnotation entity) {
		return agmSynonymService.upsert(entity);
	}

	@Override
	public ObjectResponse<AgmSynonymSlotAnnotation> create(AgmSynonymSlotAnnotation entity) {
		return agmSynonymService.upsert(entity);
	}

	public ObjectResponse<AgmSynonymSlotAnnotation> validate(AgmSynonymSlotAnnotation entity) {
		return agmSynonymService.validate(entity);
	}
}
