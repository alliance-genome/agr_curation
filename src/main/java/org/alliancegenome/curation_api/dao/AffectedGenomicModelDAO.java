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

    public List<String> findAllCuriesByTaxon(String taxonId) {
        Query jpqlQuery = entityManager.createQuery("SELECT agm.curie FROM AffectedGenomicModel agm WHERE agm.taxon.curie=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonId);
        return (List<String>) jpqlQuery.getResultList();
    }
}
