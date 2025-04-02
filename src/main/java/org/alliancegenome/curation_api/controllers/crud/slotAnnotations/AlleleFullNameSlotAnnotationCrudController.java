package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.AlleleFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.AlleleFullNameSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleFullNameSlotAnnotationCrudController extends
		BaseEntityCrudController<AlleleFullNameSlotAnnotationService, AlleleFullNameSlotAnnotation, AlleleFullNameSlotAnnotationDAO>
		implements AlleleFullNameSlotAnnotationCrudInterface {

	@Inject
	AlleleFullNameSlotAnnotationService alleleFullNameService;

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
