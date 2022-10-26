package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.VocabularyTermValidator;

@RequestScoped
public class VocabularyTermService extends BaseEntityCrudService<VocabularyTerm, VocabularyTermDAO> {

	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	VocabularyTermValidator vocabularyTermValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vocabularyTermDAO);
	}

	public ObjectResponse<VocabularyTerm> getTermInVocabulary(String name, String vocabulary) {
		VocabularyTerm term = vocabularyTermDAO.getTermInVocabulary(name, vocabulary);
		ObjectResponse<VocabularyTerm> response = new ObjectResponse<VocabularyTerm>();
		response.setEntity(term);
		return response;
	}
	
	@Override
	@Transactional
	public ObjectResponse<VocabularyTerm> create(VocabularyTerm uiEntity) {
		VocabularyTerm dbEntity = vocabularyTermValidator.validateVocabularyTermCreate(uiEntity);
		return new ObjectResponse<>(vocabularyTermDAO.persist(dbEntity));
	}
	
	@Override
	@Transactional
	public ObjectResponse<VocabularyTerm> update(VocabularyTerm uiEntity) {
		VocabularyTerm dbEntity = vocabularyTermValidator.validateVocabularyTermUpdate(uiEntity);
		return new ObjectResponse<>(vocabularyTermDAO.persist(dbEntity));
	}

}
