package org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationValidator;

@RequestScoped
public class GeneSecondaryIdSlotAnnotationService extends BaseEntityCrudService<GeneSecondaryIdSlotAnnotation, GeneSecondaryIdSlotAnnotationDAO> {

	@Inject
	GeneSecondaryIdSlotAnnotationDAO geneSecondaryIdDAO;
	@Inject
	GeneSecondaryIdSlotAnnotationValidator geneSecondaryIdValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneSecondaryIdDAO);
	}

	@Transactional
	public ObjectResponse<GeneSecondaryIdSlotAnnotation> upsert(GeneSecondaryIdSlotAnnotation uiEntity) {
		GeneSecondaryIdSlotAnnotation dbEntity = geneSecondaryIdValidator.validateGeneSecondaryIdSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<GeneSecondaryIdSlotAnnotation>(geneSecondaryIdDAO.persist(dbEntity));
	}

	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validate(GeneSecondaryIdSlotAnnotation uiEntity) {
		GeneSecondaryIdSlotAnnotation gmt = geneSecondaryIdValidator.validateGeneSecondaryIdSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<GeneSecondaryIdSlotAnnotation>(gmt);
	}

}
