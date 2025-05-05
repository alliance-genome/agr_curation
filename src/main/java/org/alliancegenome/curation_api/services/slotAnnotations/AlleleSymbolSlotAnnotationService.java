package org.alliancegenome.curation_api.services.slotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleSymbolSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleSymbolSlotAnnotationService extends BaseEntityCrudService<AlleleSymbolSlotAnnotation, AlleleSymbolSlotAnnotationDAO> {

	@Inject AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject AlleleSymbolSlotAnnotationValidator alleleSymbolValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleSymbolDAO);
	}

	@Transactional
	public ObjectResponse<AlleleSymbolSlotAnnotation> upsert(AlleleSymbolSlotAnnotation uiEntity) {
		AlleleSymbolSlotAnnotation dbEntity = alleleSymbolValidator.validateAlleleSymbolSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<AlleleSymbolSlotAnnotation>(alleleSymbolDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleSymbolSlotAnnotation> validate(AlleleSymbolSlotAnnotation uiEntity) {
		AlleleSymbolSlotAnnotation symbol = alleleSymbolValidator.validateAlleleSymbolSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleSymbolSlotAnnotation>(symbol);
	}

}
