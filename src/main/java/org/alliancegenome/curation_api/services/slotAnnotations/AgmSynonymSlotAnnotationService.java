package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.AgmSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmSynonymSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AgmSynonymSlotAnnotationService extends BaseEntityCrudService<AgmSynonymSlotAnnotation, AgmSynonymSlotAnnotationDAO> {

	@Inject AgmSynonymSlotAnnotationDAO agmSynonymDAO;
	@Inject AgmSynonymSlotAnnotationValidator agmSynonymValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmSynonymDAO);
	}

	@Transactional
	public ObjectResponse<AgmSynonymSlotAnnotation> upsert(AgmSynonymSlotAnnotation uiEntity) {
		AgmSynonymSlotAnnotation dbEntity = agmSynonymValidator.validateAgmSynonymSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<AgmSynonymSlotAnnotation>(agmSynonymDAO.persist(dbEntity));
	}

	public ObjectResponse<AgmSynonymSlotAnnotation> validate(AgmSynonymSlotAnnotation uiEntity) {
		AgmSynonymSlotAnnotation synonym = agmSynonymValidator.validateAgmSynonymSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AgmSynonymSlotAnnotation>(synonym);
	}

}
