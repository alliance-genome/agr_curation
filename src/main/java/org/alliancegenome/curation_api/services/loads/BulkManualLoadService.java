package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkManualLoadService extends BaseEntityCrudService<BulkManualLoad, BulkManualLoadDAO> {

	@Inject
	BulkManualLoadDAO bulkManualLoadDAO;
	
	@Inject
	Event<PendingBulkLoadJobEvent> pendingJobEvents;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkManualLoadDAO);
	}

	public ObjectResponse<BulkManualLoad> restartLoad(Long id) {
		ObjectResponse<BulkManualLoad> resp = updateLoad(id);
		if(resp != null) {
			pendingJobEvents.fire(new PendingBulkLoadJobEvent(id));
			return resp;
		}
		return null;
	}
	
	@Transactional
	protected ObjectResponse<BulkManualLoad> updateLoad(Long id) {
		BulkManualLoad load = bulkManualLoadDAO.find(id);
		if(load != null && load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
			return new ObjectResponse<BulkManualLoad>(load);
		}
		return null;
	}

}