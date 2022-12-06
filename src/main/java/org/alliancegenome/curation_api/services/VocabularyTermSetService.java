package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.VocabularyTermSetDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.VocabularyTermSetValidator;

@RequestScoped
public class VocabularyTermSetService extends BaseEntityCrudService<VocabularyTermSet, VocabularyTermSetDAO> {

	@Inject
	VocabularyTermSetDAO vocabularyTermSetDAO;
	@Inject
	VocabularyTermSetValidator vocabularyTermSetValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vocabularyTermSetDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<VocabularyTermSet> create(VocabularyTermSet uiEntity) {
		VocabularyTermSet dbEntity = vocabularyTermSetValidator.validateVocabularyTermSetCreate(uiEntity);
		return new ObjectResponse<>(vocabularyTermSetDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<VocabularyTermSet> update(VocabularyTermSet uiEntity) {
		VocabularyTermSet dbEntity = vocabularyTermSetValidator.validateVocabularyTermSetUpdate(uiEntity);
		return new ObjectResponse<>(vocabularyTermSetDAO.persist(dbEntity));
	}

}
