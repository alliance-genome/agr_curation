package org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneSynonymSlotAnnotationService extends BaseEntityCrudService<GeneSynonymSlotAnnotation, GeneSynonymSlotAnnotationDAO> {

	@Inject GeneSynonymSlotAnnotationDAO geneSynonymDAO;
	@Inject GeneSynonymSlotAnnotationValidator geneSynonymValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneSynonymDAO);
	}

	@Transactional
	public ObjectResponse<GeneSynonymSlotAnnotation> upsert(GeneSynonymSlotAnnotation uiEntity) {
		GeneSynonymSlotAnnotation dbEntity = geneSynonymValidator.validateGeneSynonymSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<GeneSynonymSlotAnnotation>(geneSynonymDAO.persist(dbEntity));
	}

	public ObjectResponse<GeneSynonymSlotAnnotation> validate(GeneSynonymSlotAnnotation uiEntity) {
		GeneSynonymSlotAnnotation synonym = geneSynonymValidator.validateGeneSynonymSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<GeneSynonymSlotAnnotation>(synonym);
	}

}
