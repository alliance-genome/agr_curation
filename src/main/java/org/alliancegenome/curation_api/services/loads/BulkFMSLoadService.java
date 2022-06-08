package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;

@RequestScoped
public class BulkFMSLoadService extends BaseCrudService<BulkFMSLoad, BulkFMSLoadDAO> {
	
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
		load.setStatus(JobStatus.FORCED_PENDING);
		return new ObjectResponse<BulkFMSLoad>(load);
	}
}