package org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ConstructFullNameSlotAnnotationService extends BaseEntityCrudService<ConstructFullNameSlotAnnotation, ConstructFullNameSlotAnnotationDAO> {

	@Inject ConstructFullNameSlotAnnotationDAO constructFullNameDAO;
	@Inject ConstructFullNameSlotAnnotationValidator constructFullNameValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructFullNameDAO);
	}

	@Transactional
	public ObjectResponse<ConstructFullNameSlotAnnotation> upsert(ConstructFullNameSlotAnnotation uiEntity) {
		ConstructFullNameSlotAnnotation dbEntity = constructFullNameValidator.validateConstructFullNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<ConstructFullNameSlotAnnotation>(constructFullNameDAO.persist(dbEntity));
	}

	public ObjectResponse<ConstructFullNameSlotAnnotation> validate(ConstructFullNameSlotAnnotation uiEntity) {
		ConstructFullNameSlotAnnotation sa = constructFullNameValidator.validateConstructFullNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<ConstructFullNameSlotAnnotation>(sa);
	}

}
