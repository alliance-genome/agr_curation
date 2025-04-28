package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.AgmSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmSecondaryIdSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AgmSecondaryIdSlotAnnotationService extends BaseEntityCrudService<AgmSecondaryIdSlotAnnotation, AgmSecondaryIdSlotAnnotationDAO> {

	@Inject AgmSecondaryIdSlotAnnotationDAO agmSecondaryIdDAO;
	@Inject AgmSecondaryIdSlotAnnotationValidator agmSecondaryIdValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmSecondaryIdDAO);
	}

	@Transactional
	public ObjectResponse<AgmSecondaryIdSlotAnnotation> upsert(AgmSecondaryIdSlotAnnotation uiEntity) {
		AgmSecondaryIdSlotAnnotation dbEntity = agmSecondaryIdValidator.validateAgmSecondaryIdSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<AgmSecondaryIdSlotAnnotation>(agmSecondaryIdDAO.persist(dbEntity));
	}

	public ObjectResponse<AgmSecondaryIdSlotAnnotation> validate(AgmSecondaryIdSlotAnnotation uiEntity) {
		AgmSecondaryIdSlotAnnotation amt = agmSecondaryIdValidator.validateAgmSecondaryIdSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AgmSecondaryIdSlotAnnotation>(amt);
	}

}
