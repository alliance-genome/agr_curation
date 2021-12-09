package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.*;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.services.VocabularyTermService;

@RequestScoped
public class VocabularyTermCrudController extends BaseCrudController<VocabularyTermService, VocabularyTerm, VocabularyTermDAO> implements VocabularyTermCrudInterface {

    @Inject VocabularyTermService vocabularyTermService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(vocabularyTermService);
    }

}
