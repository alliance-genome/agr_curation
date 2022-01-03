package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;

@RequestScoped
public class VocabularyService extends BaseCrudService<Vocabulary, VocabularyDAO> {

    @Inject VocabularyDAO vocabularyDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(vocabularyDAO);
    }

}
