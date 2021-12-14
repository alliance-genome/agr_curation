package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class BulkFMSLoadService extends BaseService<BulkFMSLoad, BulkFMSLoadDAO> {
    
    @Inject
    BulkFMSLoadDAO bulkFMSLoadDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkFMSLoadDAO);
    }
}