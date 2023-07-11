package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class BulkFMSLoadService extends BaseEntityCrudService<BulkFMSLoad, BulkFMSLoadDAO> {

	@Inject
	BulkFMSLoadDAO bulkFMSLoadDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkFMSLoadDAO);
	}

	@Transactional
	public ObjectResponse<BulkFMSLoad> restartLoad(Long id) {
		BulkFMSLoad load = bulkFMSLoadDAO.find(id);
		load.setBulkloadStatus(JobStatus.FORCED_PENDING);
		return new ObjectResponse<BulkFMSLoad>(load);
	}
}