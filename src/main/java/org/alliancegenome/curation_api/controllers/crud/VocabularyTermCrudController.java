package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

@RequestScoped
public class VocabularyTermCrudController extends BaseCrudController<VocabularyTermService, VocabularyTerm, VocabularyTermDAO> implements VocabularyTermCrudInterface {

    @Inject
    VocabularyTermService vocabularyTermService;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;

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

    @Override
    public ObjectResponse<VocabularyTerm> delete(Long id) {

        ObjectResponse<VocabularyTerm> ret;
        try {
            ret = vocabularyTermService.delete(id);
        } catch (ConstraintViolationException cve) {
            ret = new ObjectResponse<>();
            ret.setErrorMessage("Could not delete vocabulary term: [" + id + "]");
            throw new ApiErrorException(ret);
        } catch (Exception p) {
            String message = ExceptionUtils.getRootCauseMessage(p);
            message = "Vocabulary Term ["+get(id).getEntity().getName() + "] is still being used. \r\n" + message;
            ret = new ObjectResponse<>();
            ret.setErrorMessage(message);
            throw new ApiErrorException(ret);
        }
        return ret;
    }


}
