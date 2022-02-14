package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;

@RequestScoped
public class BulkManualLoadService extends BaseCrudService<BulkManualLoad, BulkManualLoadDAO> {
    
    @Inject
    BulkManualLoadDAO bulkManualLoadDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkManualLoadDAO);
    }
    
    @Transactional
    public ObjectResponse<BulkManualLoad> restartLoad(Long id) {
        BulkManualLoad load = bulkManualLoadDAO.find(id);
        load.setStatus(BulkLoadStatus.FORCED_PENDING);
        return new ObjectResponse<BulkManualLoad>(load);
    }
}