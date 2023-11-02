package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationService extends BaseEntityCrudService<AlleleGermlineTransmissionStatusSlotAnnotation, AlleleGermlineTransmissionStatusSlotAnnotationDAO> {

	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationDAO alleleGermlineTransmissionStatusDAO;
	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationValidator alleleGermlineTransmissionStatusValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleGermlineTransmissionStatusDAO);
	}

	@Transactional
	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> upsert(AlleleGermlineTransmissionStatusSlotAnnotation uiEntity) {
		AlleleGermlineTransmissionStatusSlotAnnotation dbEntity = alleleGermlineTransmissionStatusValidator.validateAlleleGermlineTransmissionStatusSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation>(alleleGermlineTransmissionStatusDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> validate(AlleleGermlineTransmissionStatusSlotAnnotation uiEntity) {
		AlleleGermlineTransmissionStatusSlotAnnotation agts = alleleGermlineTransmissionStatusValidator.validateAlleleGermlineTransmissionStatusSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation>(agts);
	}

}
