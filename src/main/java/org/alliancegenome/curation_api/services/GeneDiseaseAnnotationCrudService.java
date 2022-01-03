package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.GeneDiseaseAnnotationValidator;

@RequestScoped
public class GeneDiseaseAnnotationCrudService extends BaseCrudService<GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> {

    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    
    @Inject
    GeneDiseaseAnnotationValidator geneDiseaseValidator;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation uiEntity) {
        GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<GeneDiseaseAnnotation>(geneDiseaseAnnotationDAO.persist(dbEntity));
    }

}
