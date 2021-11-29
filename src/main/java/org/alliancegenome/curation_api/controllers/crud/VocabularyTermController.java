package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.services.VocabularyTermService;

@RequestScoped
public class VocabularyTermController extends BaseController<VocabularyTermService, VocabularyTerm, VocabularyTermDAO> implements VocabularyTermRESTInterface {

    @Inject VocabularyTermService vocabularyTermService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(vocabularyTermService);
    }

}
