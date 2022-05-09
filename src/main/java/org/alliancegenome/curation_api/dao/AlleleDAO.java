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

    public List<String> findAllCuriesByTaxon(String taxonId) {
        Query jpqlQuery = entityManager.createQuery("SELECT allele.curie FROM Allele allele WHERE allele.taxon.curie=:taxonId");
        jpqlQuery.setParameter("taxonId", taxonId);
        return (List<String>) jpqlQuery.getResultList();
    }
}
