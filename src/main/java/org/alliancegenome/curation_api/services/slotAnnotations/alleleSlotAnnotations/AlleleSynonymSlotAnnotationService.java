package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleSynonymSlotAnnotationService extends BaseEntityCrudService<AlleleSynonymSlotAnnotation, AlleleSynonymSlotAnnotationDAO> {

	@Inject
	AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject
	AlleleSynonymSlotAnnotationValidator alleleSynonymValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleSynonymDAO);
	}

	@Transactional
	public ObjectResponse<AlleleSynonymSlotAnnotation> upsert(AlleleSynonymSlotAnnotation uiEntity) {
		AlleleSynonymSlotAnnotation dbEntity = alleleSynonymValidator.validateAlleleSynonymSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleSynonymSlotAnnotation>(alleleSynonymDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleSynonymSlotAnnotation> validate(AlleleSynonymSlotAnnotation uiEntity) {
		AlleleSynonymSlotAnnotation synonym = alleleSynonymValidator.validateAlleleSynonymSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleSynonymSlotAnnotation>(synonym);
	}

}
