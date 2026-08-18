package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolUseSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class TransgenicToolUseSlotAnnotationService extends BaseEntityCrudService<TransgenicToolUseSlotAnnotation, TransgenicToolUseSlotAnnotationDAO> {

	@Inject TransgenicToolUseSlotAnnotationDAO transgenicToolUseDAO;
	@Inject TransgenicToolUseSlotAnnotationValidator transgenicToolUseValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolUseDAO);
	}

	@Transactional
	public ObjectResponse<TransgenicToolUseSlotAnnotation> upsert(TransgenicToolUseSlotAnnotation uiEntity) {
		TransgenicToolUseSlotAnnotation dbEntity = transgenicToolUseValidator.validateTransgenicToolUseSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<TransgenicToolUseSlotAnnotation>(transgenicToolUseDAO.persist(dbEntity));
	}

	public ObjectResponse<TransgenicToolUseSlotAnnotation> validate(TransgenicToolUseSlotAnnotation uiEntity) {
		TransgenicToolUseSlotAnnotation amt = transgenicToolUseValidator.validateTransgenicToolUseSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<TransgenicToolUseSlotAnnotation>(amt);
	}

}
