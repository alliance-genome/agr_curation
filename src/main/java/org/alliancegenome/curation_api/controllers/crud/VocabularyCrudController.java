package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyCrudInterface;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VocabularyCrudController extends BaseEntityCrudController<VocabularyService, Vocabulary, VocabularyDAO> implements VocabularyCrudInterface {

	@Inject
	VocabularyService vocabularyService;

	@Override
	@PostConstruct
	protected void init() {
		setService(vocabularyService);
	}

	@Override
	public ObjectResponse<Vocabulary> getById(Long id) {
		return vocabularyService.getById(id);
	}

	public ObjectResponse<Vocabulary> findByName(String name) {
		SearchResponse<Vocabulary> ret = findByField("name", name);
		if (ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<>();
		}
	}

	@Override
	public ObjectListResponse<VocabularyTerm> getTerms(Long id) {
		ObjectResponse<Vocabulary> vocab = vocabularyService.getById(id);
		ObjectListResponse<VocabularyTerm> terms = new ObjectListResponse<VocabularyTerm>();
		terms.setEntities(vocab.getEntity().getMemberTerms());
		return terms;
	}

}
