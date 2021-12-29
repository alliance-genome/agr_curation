package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.response.ObjectResponse;

@RequestScoped
public class BulkURLLoadService extends BaseService<BulkURLLoad, BulkURLLoadDAO> {
    
    @Inject
    BulkURLLoadDAO bulkURLLoadDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkURLLoadDAO);
    }
    
    @Transactional
    public ObjectResponse<BulkURLLoad> restartLoad(Long id) {
        BulkURLLoad load = bulkURLLoadDAO.find(id);
        load.setStatus(BulkLoadStatus.PENDING);
        return new ObjectResponse<BulkURLLoad>(load);
    }
}