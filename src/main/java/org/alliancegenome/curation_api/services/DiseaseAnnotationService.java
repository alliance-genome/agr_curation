package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.dto.Pagination;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseService<DiseaseAnnotation, DiseaseAnnotationDAO> {

    @Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
    @Inject ReferenceDAO referenceDAO;
    @Inject DoTermDAO doTermDAO;
    @Inject BiologicalEntityDAO biologicalEntityDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    @Transactional
    public void upsert(String annotationId, String doTermId, String publicationId) {

        BiologicalEntity entity = biologicalEntityDAO.find(annotationId);
        
        if(entity == null) return;

        DOTerm disease = doTermDAO.find(doTermId);
        
        if(disease == null) return;
        
        Reference reference = referenceDAO.find(publicationId);
        
        if(reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            referenceDAO.persist(reference);
        }
        
        DiseaseAnnotation da = new DiseaseAnnotation();

        da.setSubject(entity);
        da.setObject(disease);
        da.setReferenceList(List.of(reference));
        create(da);

    }


}
