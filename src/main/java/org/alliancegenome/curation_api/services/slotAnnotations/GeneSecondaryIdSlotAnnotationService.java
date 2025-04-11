package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.GeneSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.GeneSecondaryIdSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneSecondaryIdSlotAnnotationService extends BaseEntityCrudService<GeneSecondaryIdSlotAnnotation, GeneSecondaryIdSlotAnnotationDAO> {

	@Inject GeneSecondaryIdSlotAnnotationDAO geneSecondaryIdDAO;
	@Inject GeneSecondaryIdSlotAnnotationValidator geneSecondaryIdValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneSecondaryIdDAO);
	}

	@Transactional
	public ObjectResponse<GeneSecondaryIdSlotAnnotation> upsert(GeneSecondaryIdSlotAnnotation uiEntity) {
		GeneSecondaryIdSlotAnnotation dbEntity = geneSecondaryIdValidator.validateGeneSecondaryIdSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<GeneSecondaryIdSlotAnnotation>(geneSecondaryIdDAO.persist(dbEntity));
	}

	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validate(GeneSecondaryIdSlotAnnotation uiEntity) {
		GeneSecondaryIdSlotAnnotation gmt = geneSecondaryIdValidator.validateGeneSecondaryIdSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<GeneSecondaryIdSlotAnnotation>(gmt);
	}

}
