package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.response.ObjectResponse;

@RequestScoped
public class BulkLoadFileService extends BaseCrudService<BulkLoadFile, BulkLoadFileDAO> {
    
    @Inject
    BulkLoadFileDAO bulkLoadFileDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkLoadFileDAO);
    }
    
    @Transactional
    public ObjectResponse<BulkLoadFile> restartLoad(Long id) {
        BulkLoadFile load = bulkLoadFileDAO.find(id);
        load.setStatus(BulkLoadStatus.PENDING);
        return new ObjectResponse<BulkLoadFile>(load);
    }
}