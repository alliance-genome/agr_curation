package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleSynonymSlotAnnotationCrudController extends BaseEntityCrudController<AlleleSynonymSlotAnnotationService, AlleleSynonymSlotAnnotation, AlleleSynonymSlotAnnotationDAO>
	implements AlleleSynonymSlotAnnotationCrudInterface {

	@Inject
	AlleleSynonymSlotAnnotationService alleleSynonymService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleSynonymService);
	}

	@Override
	public ObjectResponse<AlleleSynonymSlotAnnotation> update(AlleleSynonymSlotAnnotation entity) {
		return alleleSynonymService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleSynonymSlotAnnotation> create(AlleleSynonymSlotAnnotation entity) {
		return alleleSynonymService.upsert(entity);
	}

	public ObjectResponse<AlleleSynonymSlotAnnotation> validate(AlleleSynonymSlotAnnotation entity) {
		return alleleSynonymService.validate(entity);
	}
}
