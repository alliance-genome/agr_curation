package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;

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

}
