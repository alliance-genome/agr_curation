package org.alliancegenome.curation_api.services;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AGMDiseaseAnnotationCrudService extends BaseCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    
    @Inject
    AGMDiseaseAnnotationValidator agmDiseaseValidator;
    
    @Inject
    DiseaseAnnotationService diseaseAnnotationService;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(agmDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
        log.info(authenticatedPerson);
        AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
    }
    
    public void runLoad(String taxonId, List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad(taxonId, annotations, "agm");
    }

}
