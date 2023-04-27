package org.alliancegenome.curation_api.services.curationreports;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.curationreports.CurationReportHistoryDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CurationReportHistoryService extends BaseEntityCrudService<CurationReportHistory, CurationReportHistoryDAO> {

	@Inject
	CurationReportHistoryDAO curationReportHistoryDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(curationReportHistoryDAO);
	}
}