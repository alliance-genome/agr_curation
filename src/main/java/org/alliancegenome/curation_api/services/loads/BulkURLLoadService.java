package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;

@RequestScoped
public class BulkURLLoadService extends BaseCrudService<BulkURLLoad, BulkURLLoadDAO> {
	
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
		if(load.getStatus().isNotRunning()) {
			load.setStatus(JobStatus.FORCED_PENDING);
		}
		return new ObjectResponse<BulkURLLoad>(load);
	}
}