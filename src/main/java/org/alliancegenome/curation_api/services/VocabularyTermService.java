package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;

@RequestScoped
public class VocabularyTermService extends BaseCrudService<VocabularyTerm, VocabularyTermDAO> {

    @Inject VocabularyTermDAO vocabularyTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(vocabularyTermDAO);
    }

}
