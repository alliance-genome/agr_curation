package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteSymbolSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteSymbolSlotAnnotationService extends BaseEntityCrudService<CassetteSymbolSlotAnnotation, CassetteSymbolSlotAnnotationDAO> {

	@Inject CassetteSymbolSlotAnnotationDAO cassetteSymbolDAO;
	@Inject CassetteSymbolSlotAnnotationValidator cassetteSymbolValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteSymbolDAO);
	}

	@Transactional
	public ObjectResponse<CassetteSymbolSlotAnnotation> upsert(CassetteSymbolSlotAnnotation uiEntity) {
		CassetteSymbolSlotAnnotation dbEntity = cassetteSymbolValidator.validateCassetteSymbolSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<CassetteSymbolSlotAnnotation>(cassetteSymbolDAO.persist(dbEntity));
	}

	public ObjectResponse<CassetteSymbolSlotAnnotation> validate(CassetteSymbolSlotAnnotation uiEntity) {
		CassetteSymbolSlotAnnotation sa = cassetteSymbolValidator.validateCassetteSymbolSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<CassetteSymbolSlotAnnotation>(sa);
	}

}
