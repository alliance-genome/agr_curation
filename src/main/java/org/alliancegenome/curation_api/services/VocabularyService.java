package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.response.ObjectResponse;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class VocabularyService extends BaseService<Vocabulary, VocabularyDAO> {

    @Inject VocabularyDAO vocabularyDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(vocabularyDAO);
    }

    @Override
    public ObjectResponse<Vocabulary> create(Vocabulary entity) {
        System.out.println("Vocab: " + entity);
        ObjectResponse<Vocabulary> ret = super.create(entity);
        System.out.println("Vocab: " + ret);
        return ret;
    }


}
