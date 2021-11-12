package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleDTO;

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
    public void processUpdate(AlleleDTO alleleDTO) {

        Allele allele = alleleDAO.find(alleleDTO.getPrimaryId());
        //log.info("Allele: " + allele + " : " + alleleDTO.getPrimaryId());
        if(allele == null) {
            allele = new Allele();
            allele.setCurie(alleleDTO.getPrimaryId());
        }
        
        allele.setSymbol(alleleDTO.getSymbol());
        allele.setDescription(alleleDTO.getDescription());
        allele.setTaxon(alleleDTO.getTaxonId());
        alleleDAO.persist(allele);

    }

}
