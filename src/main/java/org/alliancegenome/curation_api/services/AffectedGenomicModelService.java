package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelDTO;

@RequestScoped
public class AffectedGenomicModelService extends BaseCrudService<AffectedGenomicModel, AffectedGenomicModelDAO> {

    @Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(affectedGenomicModelDAO);
    }

    @Transactional
    public void processUpdate(AffectedGenomicModelDTO agm) {

        AffectedGenomicModel dbAgm = affectedGenomicModelDAO.find(agm.getPrimaryID());
        
        if(dbAgm == null) {
            dbAgm = new AffectedGenomicModel();
            dbAgm.setCurie(agm.getPrimaryID());
            dbAgm.setName(agm.getName().substring(0, Math.min(agm.getName().length(), 254)));
            dbAgm.setTaxon(agm.getTaxonId());
            dbAgm.setSubtype(agm.getSubtype());
            affectedGenomicModelDAO.persist(dbAgm);
        } else {
            if(dbAgm.getCurie().equals(agm.getPrimaryID())) {
                dbAgm.setName(agm.getName().substring(0, Math.min(agm.getName().length(), 254)));
                dbAgm.setTaxon(agm.getTaxonId());
                dbAgm.setSubtype(agm.getSubtype());
                affectedGenomicModelDAO.merge(dbAgm);
            }
        }
    }
    
}
