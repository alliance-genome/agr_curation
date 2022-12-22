package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationValidator;

@RequestScoped
public class AlleleInheritanceModeSlotAnnotationService extends BaseEntityCrudService<AlleleInheritanceModeSlotAnnotation, AlleleInheritanceModeSlotAnnotationDAO> {

	@Inject
	AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject
	AlleleInheritanceModeSlotAnnotationValidator alleleInheritanceModeValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleInheritanceModeDAO);
	}

	@Transactional
	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> upsert(AlleleInheritanceModeSlotAnnotation uiEntity) {
		AlleleInheritanceModeSlotAnnotation dbEntity = alleleInheritanceModeValidator.validateAlleleInheritanceModeSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleInheritanceModeSlotAnnotation>(alleleInheritanceModeDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> validate(AlleleInheritanceModeSlotAnnotation uiEntity) {
		AlleleInheritanceModeSlotAnnotation amt = alleleInheritanceModeValidator.validateAlleleInheritanceModeSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleInheritanceModeSlotAnnotation>(amt);
	}

}
