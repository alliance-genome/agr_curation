package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkURLLoadService extends BaseEntityCrudService<BulkURLLoad, BulkURLLoadDAO> {

	@Inject BulkURLLoadDAO bulkURLLoadDAO;

	@Inject Event<PendingBulkLoadJobEvent> pendingJobEvents;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkURLLoadDAO);
	}

	public ObjectResponse<BulkURLLoad> restartLoad(Long id) {
		Log.info("restartLoad: " + id);
		ObjectResponse<BulkURLLoad> resp = updateLoad(id);
		if (resp != null) {
			Log.info("Firing Load Pending Event: " + id);
			pendingJobEvents.fire(new PendingBulkLoadJobEvent(id));
			return resp;
		}
		return null;
	}

	@Transactional
	protected ObjectResponse<BulkURLLoad> updateLoad(Long id) {
		Log.info("updateLoad: " + id);
		BulkURLLoad load = bulkURLLoadDAO.find(id);
		if (load != null && load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
			return new ObjectResponse<BulkURLLoad>(load);
		}
		return null;
	}
}