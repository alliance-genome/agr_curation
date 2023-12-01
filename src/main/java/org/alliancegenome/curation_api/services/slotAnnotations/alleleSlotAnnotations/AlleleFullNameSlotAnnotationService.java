package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleFullNameSlotAnnotationService extends BaseEntityCrudService<AlleleFullNameSlotAnnotation, AlleleFullNameSlotAnnotationDAO> {

	@Inject
	AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject
	AlleleFullNameSlotAnnotationValidator alleleFullNameValidator;

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
