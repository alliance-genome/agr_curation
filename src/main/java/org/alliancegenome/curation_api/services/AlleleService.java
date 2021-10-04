package org.alliancegenome.curation_api.services;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.input.Pagination;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseService<Allele, AlleleDAO> {

    @Inject AlleleDAO alleleDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDAO);
    }

    @Transactional
    public void processUpdate(AlleleDTO allele) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("curie", allele.getPrimaryId());
        Allele dbAllele = get(allele.getPrimaryId());
        if(dbAllele == null) {
            dbAllele = new Allele();
            dbAllele.setCurie(allele.getPrimaryId());
            dbAllele.setSymbol(allele.getSymbol());
            dbAllele.setDescription(allele.getDescription());
            dbAllele.setTaxon(allele.getTaxonId());
            create(dbAllele);
        } else {
            if(dbAllele.getCurie().equals(allele.getPrimaryId())) {
                dbAllele.setSymbol(allele.getSymbol());
                dbAllele.setDescription(allele.getDescription());
                dbAllele.setTaxon(allele.getTaxonId());
                update(dbAllele);
            }
        }

    }

}
