package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;

@ApplicationScoped
public class BulkLoadFileDAO extends BaseSQLDAO<BulkLoadFile> {
    protected BulkLoadFileDAO() {
        super(BulkLoadFile.class);
    }
}