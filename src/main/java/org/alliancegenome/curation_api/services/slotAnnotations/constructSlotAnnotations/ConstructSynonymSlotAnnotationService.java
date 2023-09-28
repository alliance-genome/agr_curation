package org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationValidator;

@RequestScoped
public class ConstructSynonymSlotAnnotationService extends BaseEntityCrudService<ConstructSynonymSlotAnnotation, ConstructSynonymSlotAnnotationDAO> {

	@Inject
	ConstructSynonymSlotAnnotationDAO constructSynonymDAO;
	@Inject
	ConstructSynonymSlotAnnotationValidator constructSynonymValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructSynonymDAO);
	}

	@Transactional
	public ObjectResponse<ConstructSynonymSlotAnnotation> upsert(ConstructSynonymSlotAnnotation uiEntity) {
		ConstructSynonymSlotAnnotation dbEntity = constructSynonymValidator.validateConstructSynonymSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<ConstructSynonymSlotAnnotation>(constructSynonymDAO.persist(dbEntity));
	}

	public ObjectResponse<ConstructSynonymSlotAnnotation> validate(ConstructSynonymSlotAnnotation uiEntity) {
		ConstructSynonymSlotAnnotation sa = constructSynonymValidator.validateConstructSynonymSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<ConstructSynonymSlotAnnotation>(sa);
	}

}
