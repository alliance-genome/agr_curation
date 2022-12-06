package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationValidator;

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
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleSymbolSlotAnnotation>(alleleSymbolDAO.persist(dbEntity));
	}
	
	public ObjectResponse<AlleleSymbolSlotAnnotation> validate(AlleleSymbolSlotAnnotation uiEntity) {
		AlleleSymbolSlotAnnotation symbol = alleleSymbolValidator.validateAlleleSymbolSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleSymbolSlotAnnotation>(symbol);
	}
	
}
