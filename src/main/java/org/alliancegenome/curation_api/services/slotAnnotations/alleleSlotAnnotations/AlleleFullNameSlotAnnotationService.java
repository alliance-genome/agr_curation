package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationValidator;

@RequestScoped
public class AlleleFullNameSlotAnnotationService extends BaseEntityCrudService<AlleleFullNameSlotAnnotation, AlleleFullNameSlotAnnotationDAO> {

	@Inject AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject AlleleFullNameSlotAnnotationValidator alleleFullNameValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleFullNameDAO);
	}
	
	@Transactional
	public ObjectResponse<AlleleFullNameSlotAnnotation> upsert(AlleleFullNameSlotAnnotation uiEntity) {
		AlleleFullNameSlotAnnotation dbEntity = alleleFullNameValidator.validateAlleleFullNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleFullNameSlotAnnotation>(alleleFullNameDAO.persist(dbEntity));
	}
	
	public ObjectResponse<AlleleFullNameSlotAnnotation> validate(AlleleFullNameSlotAnnotation uiEntity) {
		AlleleFullNameSlotAnnotation fullName = alleleFullNameValidator.validateAlleleFullNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleFullNameSlotAnnotation>(fullName);
	}
	
}
