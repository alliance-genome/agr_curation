package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationService;

@RequestScoped
public class AlleleFullNameSlotAnnotationCrudController extends BaseEntityCrudController<AlleleFullNameSlotAnnotationService, AlleleFullNameSlotAnnotation, AlleleFullNameSlotAnnotationDAO> implements AlleleFullNameSlotAnnotationCrudInterface {

	@Inject AlleleFullNameSlotAnnotationService alleleFullNameService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(alleleFullNameService);
	}
	
	@Override
	public ObjectResponse<AlleleFullNameSlotAnnotation> update(AlleleFullNameSlotAnnotation entity) {
		return alleleFullNameService.upsert(entity);
	}
	
	@Override
	public ObjectResponse<AlleleFullNameSlotAnnotation> create(AlleleFullNameSlotAnnotation entity) {
		return alleleFullNameService.upsert(entity);
	}

	public ObjectResponse<AlleleFullNameSlotAnnotation> validate(AlleleFullNameSlotAnnotation entity) {
		return alleleFullNameService.validate(entity);
	}
}
