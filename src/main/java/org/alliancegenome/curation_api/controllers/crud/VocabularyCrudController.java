package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyCrudInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.VocabularyService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class VocabularyCrudController extends BaseController<VocabularyService, Vocabulary, VocabularyDAO> implements VocabularyCrudInterface {

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

    @Override
    public ObjectListResponse<VocabularyTerm> getTerm(Long id) {
        ObjectResponse<Vocabulary> vocab = vocabularyService.get(id);
        ObjectListResponse<VocabularyTerm> terms = new ObjectListResponse<VocabularyTerm>();
        terms.setEntities(vocab.getEntity().getMemberTerms());
        return terms;
    }

}
