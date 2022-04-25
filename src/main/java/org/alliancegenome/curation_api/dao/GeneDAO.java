package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Gene;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

    protected GeneDAO() {
        super(Gene.class);
    }
    
    public Gene getByIdOrCurie(String id) {
        return find(id);
    }

    public List<String> findAllAnnotationIds(String taxonID) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.curie FROM Gene annotation WHERE annotation.taxon.curie=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonID);
        return (List<String>) jpqlQuery.getResultList();
    }

}
