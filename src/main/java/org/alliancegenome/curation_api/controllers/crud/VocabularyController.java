package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyRESTInterface;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.services.VocabularyService;

@RequestScoped
public class VocabularyController extends BaseController<VocabularyService, Vocabulary, VocabularyDAO> implements VocabularyRESTInterface {

    @Inject VocabularyService vocabularyService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(vocabularyService);
    }

}
