package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationService extends BaseEntityCrudService<AlleleSecondaryIdSlotAnnotation, AlleleSecondaryIdSlotAnnotationDAO> {

	@Inject
	AlleleSecondaryIdSlotAnnotationDAO alleleSecondaryIdDAO;
	@Inject
	AlleleSecondaryIdSlotAnnotationValidator alleleSecondaryIdValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleSecondaryIdDAO);
	}

	@Transactional
	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> upsert(AlleleSecondaryIdSlotAnnotation uiEntity) {
		AlleleSecondaryIdSlotAnnotation dbEntity = alleleSecondaryIdValidator.validateAlleleSecondaryIdSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleSecondaryIdSlotAnnotation>(alleleSecondaryIdDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validate(AlleleSecondaryIdSlotAnnotation uiEntity) {
		AlleleSecondaryIdSlotAnnotation amt = alleleSecondaryIdValidator.validateAlleleSecondaryIdSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleSecondaryIdSlotAnnotation>(amt);
	}

}
