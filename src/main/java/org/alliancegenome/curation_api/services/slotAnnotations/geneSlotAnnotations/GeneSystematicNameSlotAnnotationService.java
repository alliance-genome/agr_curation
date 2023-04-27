package org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneSystematicNameSlotAnnotationService extends BaseEntityCrudService<GeneSystematicNameSlotAnnotation, GeneSystematicNameSlotAnnotationDAO> {

	@Inject
	GeneSystematicNameSlotAnnotationDAO geneSystematicNameDAO;
	@Inject
	GeneSystematicNameSlotAnnotationValidator geneSystematicNameValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneSystematicNameDAO);
	}

	@Transactional
	public ObjectResponse<GeneSystematicNameSlotAnnotation> upsert(GeneSystematicNameSlotAnnotation uiEntity) {
		GeneSystematicNameSlotAnnotation dbEntity = geneSystematicNameValidator.validateGeneSystematicNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<GeneSystematicNameSlotAnnotation>(geneSystematicNameDAO.persist(dbEntity));
	}

	public ObjectResponse<GeneSystematicNameSlotAnnotation> validate(GeneSystematicNameSlotAnnotation uiEntity) {
		GeneSystematicNameSlotAnnotation systematicName = geneSystematicNameValidator.validateGeneSystematicNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<GeneSystematicNameSlotAnnotation>(systematicName);
	}

}
