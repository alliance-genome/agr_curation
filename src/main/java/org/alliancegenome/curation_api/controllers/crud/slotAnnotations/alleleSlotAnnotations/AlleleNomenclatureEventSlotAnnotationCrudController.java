package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleNomenclatureEventSlotAnnotationCrudController extends
		BaseEntityCrudController<AlleleNomenclatureEventSlotAnnotationService, AlleleNomenclatureEventSlotAnnotation, AlleleNomenclatureEventSlotAnnotationDAO>
		implements AlleleNomenclatureEventSlotAnnotationCrudInterface {

	@Inject
	AlleleNomenclatureEventSlotAnnotationService alleleNomenclatureEventService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleNomenclatureEventService);
	}

	@Override
	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> update(AlleleNomenclatureEventSlotAnnotation entity) {
		return alleleNomenclatureEventService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> create(AlleleNomenclatureEventSlotAnnotation entity) {
		return alleleNomenclatureEventService.upsert(entity);
	}

	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> validate(
			AlleleNomenclatureEventSlotAnnotation entity) {
		return alleleNomenclatureEventService.validate(entity);
	}
}
