package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneFullNameSlotAnnotationCrudController extends BaseEntityCrudController<GeneFullNameSlotAnnotationService, GeneFullNameSlotAnnotation, GeneFullNameSlotAnnotationDAO>
	implements GeneFullNameSlotAnnotationCrudInterface {

	@Inject
	GeneFullNameSlotAnnotationService geneFullNameService;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneFullNameService);
	}

	@Override
	public ObjectResponse<GeneFullNameSlotAnnotation> update(GeneFullNameSlotAnnotation entity) {
		return geneFullNameService.upsert(entity);
	}

	@Override
	public ObjectResponse<GeneFullNameSlotAnnotation> create(GeneFullNameSlotAnnotation entity) {
		return geneFullNameService.upsert(entity);
	}

	public ObjectResponse<GeneFullNameSlotAnnotation> validate(GeneFullNameSlotAnnotation entity) {
		return geneFullNameService.validate(entity);
	}
}
