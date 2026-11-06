package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolFullNameSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class TransgenicToolFullNameSlotAnnotationService extends BaseEntityCrudService<TransgenicToolFullNameSlotAnnotation, TransgenicToolFullNameSlotAnnotationDAO> {

	@Inject TransgenicToolFullNameSlotAnnotationDAO transgenicToolFullNameDAO;
	@Inject TransgenicToolFullNameSlotAnnotationValidator transgenicToolFullNameValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolFullNameDAO);
	}

	@Transactional
	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> upsert(TransgenicToolFullNameSlotAnnotation uiEntity) {
		TransgenicToolFullNameSlotAnnotation dbEntity = transgenicToolFullNameValidator.validateTransgenicToolFullNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<TransgenicToolFullNameSlotAnnotation>(transgenicToolFullNameDAO.persist(dbEntity));
	}

	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> validate(TransgenicToolFullNameSlotAnnotation uiEntity) {
		TransgenicToolFullNameSlotAnnotation sa = transgenicToolFullNameValidator.validateTransgenicToolFullNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<TransgenicToolFullNameSlotAnnotation>(sa);
	}

}
