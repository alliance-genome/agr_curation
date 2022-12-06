package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class VocabularyTermDAO extends BaseSQLDAO<VocabularyTerm> {

	protected VocabularyTermDAO() {
		super(VocabularyTerm.class);
	}

	public VocabularyTerm getTermInVocabulary(String vocabularyName, String termName) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", termName);
		params.put("vocabulary.name", vocabularyName);

		SearchResponse<VocabularyTerm> resp = findByParams(null, params);
		return resp.getSingleResult();

	}

	public VocabularyTerm getTermInVocabularyTermSet(String vocabularyTermSetName, String termName) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", termName);
		params.put("vocabularyTermSets.name", vocabularyTermSetName);

		SearchResponse<VocabularyTerm> resp = findByParams(null, params);
		return resp.getSingleResult();

	}
}
