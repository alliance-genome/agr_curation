package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;

@ApplicationScoped
public class AGMDiseaseAnnotationDAO extends BaseSQLDAO<AGMDiseaseAnnotation> {

    protected AGMDiseaseAnnotationDAO() {
        super(AGMDiseaseAnnotation.class);
    }

    public List<String> findAllAnnotationIds(String taxonID) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.uniqueId FROM AGMDiseaseAnnotation annotation WHERE annotation.subject.taxon=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonID);
        return (List<String>) jpqlQuery.getResultList();
    }
    
}
