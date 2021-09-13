package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import java.util.List;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

    protected DiseaseAnnotationDAO() {
        super(DiseaseAnnotation.class);
    }

    public List<String> findAllAnnotationIDs(String taxonID) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.curie FROM DiseaseAnnotation annotation WHERE annotation.subject.taxon=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonID);
        return (List<String>) jpqlQuery.getResultList();
    }
}
