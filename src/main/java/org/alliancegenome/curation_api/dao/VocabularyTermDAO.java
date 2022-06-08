package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.SearchResponse;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class VocabularyTermDAO extends BaseSQLDAO<VocabularyTerm> {

	@Inject
	VocabularyDAO vocabularyDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	
	protected VocabularyTermDAO() {
		super(VocabularyTerm.class);
	}
	
	public VocabularyTerm getTermInVocabulary (String termName, String vocabularyName) {
		
		Vocabulary vocabulary;
		SearchResponse<Vocabulary> vocabularySearchResponse = vocabularyDAO.findByField("name", vocabularyName);
		if (vocabularySearchResponse == null || vocabularySearchResponse.getSingleResult() == null) {
			return null;
		} 
		vocabulary = vocabularySearchResponse.getSingleResult();
		
		VocabularyTerm term = vocabulary.getMemberTerms().stream().filter(
				vocabularyTerm -> termName.equals(vocabularyTerm.getName())
			).findAny().orElse(null);
		
		return term;
	}
}
