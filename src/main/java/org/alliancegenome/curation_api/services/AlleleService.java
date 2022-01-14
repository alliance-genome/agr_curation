package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleValidator;

@RequestScoped
public class AlleleService extends BaseCrudService<Allele, AlleleDAO> {

    @Inject AlleleDAO alleleDAO;
    @Inject AlleleValidator alleleValidator;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<Allele> update(Allele uiEntity) {
        Allele dbEntity = alleleValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
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
