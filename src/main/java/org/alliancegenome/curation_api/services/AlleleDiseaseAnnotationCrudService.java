package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleDiseaseAnnotationValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleDiseaseAnnotationCrudService extends BaseCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDAO> {

    @Inject
    AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    
    @Inject
    AlleleDiseaseAnnotationValidator alleleDiseaseValidator;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation uiEntity) {
        log.info(authenticatedPerson);
        AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<AlleleDiseaseAnnotation>(alleleDiseaseAnnotationDAO.persist(dbEntity));
    }

}
