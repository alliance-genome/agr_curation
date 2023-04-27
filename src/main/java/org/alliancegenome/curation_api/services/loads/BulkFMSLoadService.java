package org.alliancegenome.curation_api.services.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

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