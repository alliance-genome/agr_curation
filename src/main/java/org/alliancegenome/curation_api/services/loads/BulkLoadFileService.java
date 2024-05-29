package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadFileJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkLoadFileService extends BaseEntityCrudService<BulkLoadFile, BulkLoadFileDAO> {

	@Inject BulkLoadFileDAO bulkLoadFileDAO;

	@Inject Event<PendingBulkLoadFileJobEvent> pendingFileJobEvents;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileDAO);
	}

	public ObjectResponse<BulkLoadFile> restartLoad(Long id) {
		ObjectResponse<BulkLoadFile> resp = updateLoad(id);
		if (resp != null) {
			pendingFileJobEvents.fire(new PendingBulkLoadFileJobEvent(id));
			return resp;
		}
		return null;
	}

	@Transactional
	protected ObjectResponse<BulkLoadFile> updateLoad(Long id) {
		BulkLoadFile load = bulkLoadFileDAO.find(id);
		if (load != null && load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
			return new ObjectResponse<BulkLoadFile>(load);
		}
		return null;
	}

}