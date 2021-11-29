package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

    protected DiseaseAnnotationDAO() {
        super(DiseaseAnnotation.class);
    }

    public List<String> findAllAnnotationCuries(String taxonID) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.curie FROM DiseaseAnnotation annotation WHERE annotation.subject.taxon=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonID);
        return (List<String>) jpqlQuery.getResultList();
    }
    
    public Long getIdFromCurie(String curie) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.id FROM DiseaseAnnotation annotation WHERE annotation.curie=:curie");
        jpqlQuery.setParameter("curie", curie);
        return(Long) jpqlQuery.getSingleResult();
    }
}
