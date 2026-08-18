package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteComponentSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteComponentSlotAnnotationService extends BaseEntityCrudService<CassetteComponentSlotAnnotation, CassetteComponentSlotAnnotationDAO> {

	@Inject CassetteComponentSlotAnnotationDAO cassetteComponentDAO;
	@Inject CassetteComponentSlotAnnotationValidator cassetteComponentValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteComponentDAO);
	}

	@Transactional
	public ObjectResponse<CassetteComponentSlotAnnotation> upsert(CassetteComponentSlotAnnotation uiEntity) {
		CassetteComponentSlotAnnotation dbEntity = cassetteComponentValidator.validateCassetteComponentSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<CassetteComponentSlotAnnotation>(cassetteComponentDAO.persist(dbEntity));
	}

	public ObjectResponse<CassetteComponentSlotAnnotation> validate(CassetteComponentSlotAnnotation uiEntity) {
		CassetteComponentSlotAnnotation amt = cassetteComponentValidator.validateCassetteComponentSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<CassetteComponentSlotAnnotation>(amt);
	}

}
