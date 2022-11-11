package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@ApplicationScoped
public class BulkLoadFileHistoryService extends BaseEntityCrudService<BulkLoadFileHistory, BulkLoadFileHistoryDAO> {
	
	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileHistoryDAO);
	}
	
}