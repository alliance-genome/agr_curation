package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.geneSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationService;

@RequestScoped
public class GeneSynonymSlotAnnotationCrudController extends BaseEntityCrudController<GeneSynonymSlotAnnotationService, GeneSynonymSlotAnnotation, GeneSynonymSlotAnnotationDAO> implements GeneSynonymSlotAnnotationCrudInterface {

	@Inject GeneSynonymSlotAnnotationService geneSynonymService;
	
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
