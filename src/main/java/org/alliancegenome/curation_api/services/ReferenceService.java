package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Reference;

@RequestScoped
public class ReferenceService extends BaseService<Reference, ReferenceDAO> {

    @Inject
    ReferenceDAO referenceDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(referenceDAO);
    }
    
}
