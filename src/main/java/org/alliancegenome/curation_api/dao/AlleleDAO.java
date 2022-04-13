package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {

    protected AlleleDAO() {
        super(Allele.class);
    }

    public List<String> findAllAnnotationIds(String taxonID) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.curie FROM Allele annotation WHERE annotation.taxon.curie=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonID);
        return (List<String>) jpqlQuery.getResultList();
    }
}
