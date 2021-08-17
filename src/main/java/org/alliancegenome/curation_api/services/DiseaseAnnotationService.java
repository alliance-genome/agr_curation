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
    @Inject GeneDAO geneDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }
    
    public SearchResults<DiseaseAnnotation> getAllDiseaseAnnotation(Pagination pagination) {
        return getAll(pagination);
    }

    public void upsert(String geneId, String doTermId, String publicationId) {
        
        DiseaseAnnotation da = new DiseaseAnnotation();

        Gene gene = geneDAO.find(geneId);
        
        if(gene == null) {
            gene = new Gene();
            gene.setCurie(geneId);
            geneDAO.persist(gene);
        }
        
        DOTerm disease = doTermDAO.find(doTermId);
        
        if(disease == null) {
            disease = new DOTerm();
            disease.setCurie(doTermId);
            doTermDAO.persist(disease);
        }
        
        Reference reference = referenceDAO.find(publicationId);
        
        if(reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            referenceDAO.persist(reference);
        }

        da.setSubject(gene);
        da.setObject(disease);
        da.setReferenceList(List.of(reference));
        create(da);

    }


}
