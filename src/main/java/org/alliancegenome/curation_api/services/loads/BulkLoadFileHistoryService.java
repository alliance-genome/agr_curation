package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkLoadFileHistoryService extends BaseEntityCrudService<BulkLoadFileHistory, BulkLoadFileHistoryDAO> {

	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileHistoryDAO);
	}

}