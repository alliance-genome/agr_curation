package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.entities.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class BiologicalEntityService extends BaseService<BiologicalEntity, BiologicalEntityDAO> {

    @Inject
    BiologicalEntityDAO biologicalEntityDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(biologicalEntityDAO);
    }
    
}
