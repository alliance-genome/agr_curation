package org.alliancegenome.curation_api.services.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkLoadFileService extends BaseEntityCrudService<BulkLoadFile, BulkLoadFileDAO> {

	@Inject
	BulkLoadFileDAO bulkLoadFileDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileDAO);
	}

	@Transactional
	public ObjectResponse<BulkLoadFile> restartLoad(Long id) {
		BulkLoadFile load = bulkLoadFileDAO.find(id);
		if (load.getBulkloadStatus().isNotRunning()) {
			load.setBulkloadStatus(JobStatus.FORCED_PENDING);
		}
		return new ObjectResponse<BulkLoadFile>(load);
	}
}