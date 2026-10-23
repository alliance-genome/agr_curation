package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteSynonymSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteSynonymSlotAnnotationService extends BaseEntityCrudService<CassetteSynonymSlotAnnotation, CassetteSynonymSlotAnnotationDAO> {

	@Inject CassetteSynonymSlotAnnotationDAO cassetteSynonymDAO;
	@Inject CassetteSynonymSlotAnnotationValidator cassetteSynonymValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteSynonymDAO);
	}

	@Transactional
	public ObjectResponse<CassetteSynonymSlotAnnotation> upsert(CassetteSynonymSlotAnnotation uiEntity) {
		CassetteSynonymSlotAnnotation dbEntity = cassetteSynonymValidator.validateCassetteSynonymSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<CassetteSynonymSlotAnnotation>(cassetteSynonymDAO.persist(dbEntity));
	}

	public ObjectResponse<CassetteSynonymSlotAnnotation> validate(CassetteSynonymSlotAnnotation uiEntity) {
		CassetteSynonymSlotAnnotation sa = cassetteSynonymValidator.validateCassetteSynonymSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<CassetteSynonymSlotAnnotation>(sa);
	}

}
