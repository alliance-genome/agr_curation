package org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ConstructComponentSlotAnnotationService extends BaseEntityCrudService<ConstructComponentSlotAnnotation, ConstructComponentSlotAnnotationDAO> {

	@Inject
	ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject
	ConstructComponentSlotAnnotationValidator constructComponentValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructComponentDAO);
	}

	@Transactional
	public ObjectResponse<ConstructComponentSlotAnnotation> upsert(ConstructComponentSlotAnnotation uiEntity) {
		ConstructComponentSlotAnnotation dbEntity = constructComponentValidator.validateConstructComponentSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<ConstructComponentSlotAnnotation>(constructComponentDAO.persist(dbEntity));
	}

	public ObjectResponse<ConstructComponentSlotAnnotation> validate(ConstructComponentSlotAnnotation uiEntity) {
		ConstructComponentSlotAnnotation amt = constructComponentValidator.validateConstructComponentSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<ConstructComponentSlotAnnotation>(amt);
	}

}
