package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;

@RequestScoped
public class VocabularyTermCrudController extends BaseCrudController<VocabularyTermService, VocabularyTerm, VocabularyTermDAO> implements VocabularyTermCrudInterface {

    @Inject VocabularyTermService vocabularyTermService;
    @Inject VocabularyTermDAO vocabularyTermDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(vocabularyTermService);
    }

    @Override
    public ObjectResponse<VocabularyTerm> getTermInVocabulary(String name, String vocabulary) {
        VocabularyTerm term = vocabularyTermDAO.getTermInVocabulary(name, vocabulary);
        ObjectResponse<VocabularyTerm> response = new ObjectResponse<VocabularyTerm>();
        response.setEntity(term);
        return response;
    }

}
