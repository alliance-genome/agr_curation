package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class BulkURLLoadService extends BaseService<BulkURLLoad, BulkURLLoadDAO> {
    
    @Inject
    BulkURLLoadDAO bulkURLLoadDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkURLLoadDAO);
    }
}