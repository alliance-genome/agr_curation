package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AGMDiseaseAnnotationCrudService extends BaseService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    
    @Inject
    AGMDiseaseAnnotationValidator agmDiseaseValidator;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(agmDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
        AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<AGMDiseaseAnnotation>(agmDiseaseAnnotationDAO.persist(dbEntity));
    }

}
