package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteFullNameSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteFullNameSlotAnnotationService extends BaseEntityCrudService<CassetteFullNameSlotAnnotation, CassetteFullNameSlotAnnotationDAO> {

	@Inject CassetteFullNameSlotAnnotationDAO cassetteFullNameDAO;
	@Inject CassetteFullNameSlotAnnotationValidator cassetteFullNameValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteFullNameDAO);
	}

	@Transactional
	public ObjectResponse<CassetteFullNameSlotAnnotation> upsert(CassetteFullNameSlotAnnotation uiEntity) {
		CassetteFullNameSlotAnnotation dbEntity = cassetteFullNameValidator.validateCassetteFullNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<CassetteFullNameSlotAnnotation>(cassetteFullNameDAO.persist(dbEntity));
	}

	public ObjectResponse<CassetteFullNameSlotAnnotation> validate(CassetteFullNameSlotAnnotation uiEntity) {
		CassetteFullNameSlotAnnotation sa = cassetteFullNameValidator.validateCassetteFullNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<CassetteFullNameSlotAnnotation>(sa);
	}

}
