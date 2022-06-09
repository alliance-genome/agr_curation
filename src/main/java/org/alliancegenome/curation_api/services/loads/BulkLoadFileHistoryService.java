package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;

@RequestScoped
public class BulkLoadFileHistoryService extends BaseCrudService<BulkLoadFileHistory, BulkLoadFileHistoryDAO> {
	
	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileHistoryDAO);
	}
	
}