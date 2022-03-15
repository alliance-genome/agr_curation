package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;

@RequestScoped
public class GenomicEntityService extends BaseCrudService<GenomicEntity, GenomicEntityDAO> {

    @Inject
    GenomicEntityDAO genomicEntityDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(genomicEntityDAO);
    }
    
}
