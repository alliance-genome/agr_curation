package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyCrudInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.VocabularyService;

@RequestScoped
public class VocabularyCrudController extends BaseEntityCrudController<VocabularyService, Vocabulary, VocabularyDAO> implements VocabularyCrudInterface {

	@Inject VocabularyService vocabularyService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(vocabularyService);
	}

	@Override
	public ObjectResponse<Vocabulary> get(Long id) {
		return vocabularyService.get(id);
	}
	
	public ObjectResponse<Vocabulary> findByName(String name) {
		SearchResponse<Vocabulary> ret = findByField("name", name);
		if(ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<>();
		}
	}

	@Override
	public ObjectListResponse<VocabularyTerm> getTerms(Long id) {
		ObjectResponse<Vocabulary> vocab = vocabularyService.get(id);
		ObjectListResponse<VocabularyTerm> terms = new ObjectListResponse<VocabularyTerm>();
		terms.setEntities(vocab.getEntity().getMemberTerms());
		return terms;
	}

}
