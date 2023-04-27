package org.alliancegenome.curation_api.services.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkManualLoadService extends BaseEntityCrudService<BulkManualLoad, BulkManualLoadDAO> {

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
		if (load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
		}
		return new ObjectResponse<BulkManualLoad>(load);
	}
}