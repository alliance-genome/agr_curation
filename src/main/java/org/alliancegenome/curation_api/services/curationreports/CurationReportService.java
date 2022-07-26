package org.alliancegenome.curation_api.services.curationreports;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseEntityCrudService;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;
import org.alliancegenome.curation_api.response.ObjectResponse;

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
		if(report.getCurationReportStatus().isNotRunning()) {
			report.setCurationReportStatus(JobStatus.FORCED_PENDING);
		}
		return new ObjectResponse<CurationReport>(report);
	}
}