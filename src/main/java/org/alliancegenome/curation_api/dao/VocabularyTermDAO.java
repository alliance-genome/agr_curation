package org.alliancegenome.curation_api.dao;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.SearchResponse;

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
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", termName);
        params.put("vocabulary_id", vocabulary.getId());
        SearchResponse<VocabularyTerm> vocabularyTermSearchResponse = vocabularyTermDAO.findByParams(null, params);
        if (vocabularyTermSearchResponse == null || vocabularyTermSearchResponse.getSingleResult() == null) {
            return null;
        }
        return vocabularyTermSearchResponse.getSingleResult();
    }
}
