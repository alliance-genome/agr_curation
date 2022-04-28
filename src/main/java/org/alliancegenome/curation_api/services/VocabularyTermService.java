package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.VocabularyTermValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@RequestScoped
public class VocabularyTermService extends BaseCrudService<VocabularyTerm, VocabularyTermDAO> {

    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    VocabularyTermValidator vocabularyTermValidator;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(vocabularyTermDAO);
    }

    @Override
    @Transactional
    public ObjectResponse<VocabularyTerm> update(VocabularyTerm uiEntity) {
        VocabularyTerm dbEntity = vocabularyTermValidator.validateVocabularyTerm(uiEntity);
        return new ObjectResponse<>(vocabularyTermDAO.persist(dbEntity));
    }

    @Override
    @Transactional
    public ObjectResponse<VocabularyTerm> delete(Long id) {
        VocabularyTerm object = vocabularyTermDAO.remove(id);
        return new ObjectResponse<>(object);
    }

}
