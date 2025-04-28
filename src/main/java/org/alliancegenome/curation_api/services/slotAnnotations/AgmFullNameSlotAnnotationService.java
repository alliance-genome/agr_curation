package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.AgmFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmFullNameSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AgmFullNameSlotAnnotationService extends BaseEntityCrudService<AgmFullNameSlotAnnotation, AgmFullNameSlotAnnotationDAO> {

	@Inject AgmFullNameSlotAnnotationDAO agmFullNameDAO;
	@Inject AgmFullNameSlotAnnotationValidator agmFullNameValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmFullNameDAO);
	}

	@Transactional
	public ObjectResponse<AgmFullNameSlotAnnotation> upsert(AgmFullNameSlotAnnotation uiEntity) {
		AgmFullNameSlotAnnotation dbEntity = agmFullNameValidator.validateAgmFullNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<AgmFullNameSlotAnnotation>(agmFullNameDAO.persist(dbEntity));
	}

	public ObjectResponse<AgmFullNameSlotAnnotation> validate(AgmFullNameSlotAnnotation uiEntity) {
		AgmFullNameSlotAnnotation fullName = agmFullNameValidator.validateAgmFullNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AgmFullNameSlotAnnotation>(fullName);
	}

}
