package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkFMSLoadService extends BaseEntityCrudService<BulkFMSLoad, BulkFMSLoadDAO> {

	@Inject BulkFMSLoadDAO bulkFMSLoadDAO;

	@Inject Event<PendingBulkLoadJobEvent> pendingJobEvents;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkFMSLoadDAO);
	}

	public ObjectResponse<BulkFMSLoad> restartLoad(Long id) {
		ObjectResponse<BulkFMSLoad> resp = updateLoad(id);
		if (resp != null) {
			pendingJobEvents.fire(new PendingBulkLoadJobEvent(id));
			return resp;
		}
		return null;
	}

	@Transactional
	protected ObjectResponse<BulkFMSLoad> updateLoad(Long id) {
		BulkFMSLoad load = bulkFMSLoadDAO.find(id);
		if (load != null && load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
			return new ObjectResponse<BulkFMSLoad>(load);
		}
		return null;
	}
}