package org.alliancegenome.curation_api.services.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkURLLoadService extends BaseEntityCrudService<BulkURLLoad, BulkURLLoadDAO> {

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
		if (load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
		}
		return new ObjectResponse<BulkURLLoad>(load);
	}
}