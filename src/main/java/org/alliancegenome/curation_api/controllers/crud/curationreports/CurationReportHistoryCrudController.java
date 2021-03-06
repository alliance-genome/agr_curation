package org.alliancegenome.curation_api.controllers.crud.curationreports;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportHistoryDAO;
import org.alliancegenome.curation_api.interfaces.curationreports.CurationReportHistoryCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;
import org.alliancegenome.curation_api.services.curationreports.CurationReportHistoryService;

@RequestScoped
public class CurationReportHistoryCrudController extends BaseCrudController<CurationReportHistoryService, CurationReportHistory, CurationReportHistoryDAO> implements CurationReportHistoryCrudInterface {

	@Inject CurationReportHistoryService curationReportHistoryService;

	@Override
	@PostConstruct
	protected void init() {
		setService(curationReportHistoryService);
	}

}
