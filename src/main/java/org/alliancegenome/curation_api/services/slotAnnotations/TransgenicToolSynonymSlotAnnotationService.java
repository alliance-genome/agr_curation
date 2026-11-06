package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSynonymSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class TransgenicToolSynonymSlotAnnotationService extends BaseEntityCrudService<TransgenicToolSynonymSlotAnnotation, TransgenicToolSynonymSlotAnnotationDAO> {

	@Inject TransgenicToolSynonymSlotAnnotationDAO transgenicToolSynonymDAO;
	@Inject TransgenicToolSynonymSlotAnnotationValidator transgenicToolSynonymValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolSynonymDAO);
	}

	@Transactional
	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> upsert(TransgenicToolSynonymSlotAnnotation uiEntity) {
		TransgenicToolSynonymSlotAnnotation dbEntity = transgenicToolSynonymValidator.validateTransgenicToolSynonymSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<TransgenicToolSynonymSlotAnnotation>(transgenicToolSynonymDAO.persist(dbEntity));
	}

	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> validate(TransgenicToolSynonymSlotAnnotation uiEntity) {
		TransgenicToolSynonymSlotAnnotation sa = transgenicToolSynonymValidator.validateTransgenicToolSynonymSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<TransgenicToolSynonymSlotAnnotation>(sa);
	}

}
