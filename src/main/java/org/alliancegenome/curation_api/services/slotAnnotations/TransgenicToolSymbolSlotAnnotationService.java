package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSymbolSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class TransgenicToolSymbolSlotAnnotationService extends BaseEntityCrudService<TransgenicToolSymbolSlotAnnotation, TransgenicToolSymbolSlotAnnotationDAO> {

	@Inject TransgenicToolSymbolSlotAnnotationDAO transgenicToolSymbolDAO;
	@Inject TransgenicToolSymbolSlotAnnotationValidator transgenicToolSymbolValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolSymbolDAO);
	}

	@Transactional
	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> upsert(TransgenicToolSymbolSlotAnnotation uiEntity) {
		TransgenicToolSymbolSlotAnnotation dbEntity = transgenicToolSymbolValidator.validateTransgenicToolSymbolSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<TransgenicToolSymbolSlotAnnotation>(transgenicToolSymbolDAO.persist(dbEntity));
	}

	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> validate(TransgenicToolSymbolSlotAnnotation uiEntity) {
		TransgenicToolSymbolSlotAnnotation sa = transgenicToolSymbolValidator.validateTransgenicToolSymbolSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<TransgenicToolSymbolSlotAnnotation>(sa);
	}

}
