package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSystematicNameSlotAnnotationCrudController extends
	BaseEntityCrudController<GeneSystematicNameSlotAnnotationService, GeneSystematicNameSlotAnnotation, GeneSystematicNameSlotAnnotationDAO> implements GeneSystematicNameSlotAnnotationCrudInterface {

	@Inject
	GeneSystematicNameSlotAnnotationService geneSystematicNameService;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneSystematicNameService);
	}

	@Override
	public ObjectResponse<GeneSystematicNameSlotAnnotation> update(GeneSystematicNameSlotAnnotation entity) {
		return geneSystematicNameService.upsert(entity);
	}

	@Override
	public ObjectResponse<GeneSystematicNameSlotAnnotation> create(GeneSystematicNameSlotAnnotation entity) {
		return geneSystematicNameService.upsert(entity);
	}

	public ObjectResponse<GeneSystematicNameSlotAnnotation> validate(GeneSystematicNameSlotAnnotation entity) {
		return geneSystematicNameService.validate(entity);
	}
}
