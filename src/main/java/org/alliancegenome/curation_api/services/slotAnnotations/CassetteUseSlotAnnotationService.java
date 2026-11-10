package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteUseSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteUseSlotAnnotationService extends BaseEntityCrudService<CassetteUseSlotAnnotation, CassetteUseSlotAnnotationDAO> {

	@Inject CassetteUseSlotAnnotationDAO cassetteUseDAO;
	@Inject CassetteUseSlotAnnotationValidator cassetteUseValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteUseDAO);
	}

	@Transactional
	public ObjectResponse<CassetteUseSlotAnnotation> upsert(CassetteUseSlotAnnotation uiEntity) {
		CassetteUseSlotAnnotation dbEntity = cassetteUseValidator.validateCassetteUseSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<CassetteUseSlotAnnotation>(cassetteUseDAO.persist(dbEntity));
	}

	public ObjectResponse<CassetteUseSlotAnnotation> validate(CassetteUseSlotAnnotation uiEntity) {
		CassetteUseSlotAnnotation sa = cassetteUseValidator.validateCassetteUseSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<CassetteUseSlotAnnotation>(sa);
	}

}
