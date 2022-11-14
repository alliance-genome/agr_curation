package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AlleleMutationTypeSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AlleleMutationTypeSlotAnnotationService;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationCrudController extends BaseEntityCrudController<AlleleMutationTypeSlotAnnotationService, AlleleMutationTypeSlotAnnotation, AlleleMutationTypeSlotAnnotationDAO> implements AlleleMutationTypeSlotAnnotationCrudInterface {

	@Inject AlleleMutationTypeSlotAnnotationService alleleMutationTypeService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(alleleMutationTypeService);
	}
	
	@Override
	public ObjectResponse<AlleleMutationTypeSlotAnnotation> update(AlleleMutationTypeSlotAnnotation entity) {
		return alleleMutationTypeService.upsert(entity);
	}
	
	@Override
	public ObjectResponse<AlleleMutationTypeSlotAnnotation> create(AlleleMutationTypeSlotAnnotation entity) {
		return alleleMutationTypeService.upsert(entity);
	}

	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validate(AlleleMutationTypeSlotAnnotation entity) {
		return alleleMutationTypeService.validate(entity);
	}
}
