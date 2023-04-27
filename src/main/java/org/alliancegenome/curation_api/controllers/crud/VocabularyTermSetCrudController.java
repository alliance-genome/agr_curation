package org.alliancegenome.curation_api.controllers.crud;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.VocabularyTermSetDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyTermSetCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermSetService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class VocabularyTermSetCrudController extends BaseEntityCrudController<VocabularyTermSetService, VocabularyTermSet, VocabularyTermSetDAO> implements VocabularyTermSetCrudInterface {

	@Inject
	VocabularyTermSetService vocabularyTermSetService;

	@Override
	@PostConstruct
	protected void init() {
		setService(vocabularyTermSetService);
	}

	public ObjectResponse<VocabularyTermSet> findByName(String name) {
		SearchResponse<VocabularyTermSet> ret = findByField("name", name);
		if (ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<>();
		}
	}

	@Override
	public ObjectListResponse<VocabularyTerm> getTerms(Long id) {
		ObjectResponse<VocabularyTermSet> vocab = vocabularyTermSetService.get(id);
		ObjectListResponse<VocabularyTerm> terms = new ObjectListResponse<VocabularyTerm>();
		terms.setEntities(vocab.getEntity().getMemberTerms());
		return terms;
	}
}
