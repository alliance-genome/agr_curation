package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;

@ApplicationScoped
public class AffectedGenomicModelDAO extends BaseSQLDAO<AffectedGenomicModel> {

    protected AffectedGenomicModelDAO() {
        super(AffectedGenomicModel.class);
    }

    public List<String> findAllAnnotationIds(String taxonID) {
        Query jpqlQuery = entityManager.createQuery("SELECT annotation.curie FROM AffectedGenomicModel annotation WHERE annotation.taxon.curie=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonID);
        return (List<String>) jpqlQuery.getResultList();
    }
}
