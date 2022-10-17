package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.VocabularyValidator;

@RequestScoped
public class VocabularyService extends BaseEntityCrudService<Vocabulary, VocabularyDAO> {

	@Inject VocabularyDAO vocabularyDAO;
	@Inject VocabularyValidator vocabularyValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vocabularyDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Vocabulary> update(Vocabulary uiEntity) {
		Vocabulary dbEntity = vocabularyValidator.validateVocabularyUpdate(uiEntity);
		return new ObjectResponse<>(vocabularyDAO.persist(dbEntity));
	}
	
	@Override
	@Transactional
	public ObjectResponse<Vocabulary> create(Vocabulary uiEntity) {
		Vocabulary dbEntity = vocabularyValidator.validateVocabularyCreate(uiEntity);
		return new ObjectResponse<>(vocabularyDAO.persist(dbEntity));
	}
}
