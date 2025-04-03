package org.alliancegenome.curation_api.controllers.crud.slotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.GeneSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.GeneSecondaryIdSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.GeneSecondaryIdSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSecondaryIdSlotAnnotationCrudController extends
	BaseEntityCrudController<GeneSecondaryIdSlotAnnotationService, GeneSecondaryIdSlotAnnotation, GeneSecondaryIdSlotAnnotationDAO> implements GeneSecondaryIdSlotAnnotationCrudInterface {

	@Inject
	GeneSecondaryIdSlotAnnotationService geneSecondaryIdService;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneSecondaryIdService);
	}

	@Override
	public ObjectResponse<GeneSecondaryIdSlotAnnotation> update(GeneSecondaryIdSlotAnnotation entity) {
		return geneSecondaryIdService.upsert(entity);
	}

	@Override
	public ObjectResponse<GeneSecondaryIdSlotAnnotation> create(GeneSecondaryIdSlotAnnotation entity) {
		return geneSecondaryIdService.upsert(entity);
	}

	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validate(GeneSecondaryIdSlotAnnotation entity) {
		return geneSecondaryIdService.validate(entity);
	}
}
