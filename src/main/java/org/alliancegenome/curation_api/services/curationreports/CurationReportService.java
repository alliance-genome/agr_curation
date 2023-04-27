package org.alliancegenome.curation_api.services.curationreports;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.curationreports.CurationReportDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class CurationReportService extends BaseEntityCrudService<CurationReport, CurationReportDAO> {

	@Inject
	CurationReportDAO curationReportDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(curationReportDAO);
	}

	@Transactional
	public ObjectResponse<CurationReport> restartReport(Long id) {
		CurationReport report = curationReportDAO.find(id);
		if (report.getCurationReportStatus().isNotRunning()) {
			report.setCurationReportStatus(JobStatus.FORCED_PENDING);
		}
		return new ObjectResponse<CurationReport>(report);
	}
}