package org.alliancegenome.curation_api.controllers.crud.curationreports;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportHistoryDAO;
import org.alliancegenome.curation_api.interfaces.curationreports.CurationReportHistoryCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;
import org.alliancegenome.curation_api.services.curationreports.CurationReportHistoryService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CurationReportHistoryCrudController extends BaseEntityCrudController<CurationReportHistoryService, CurationReportHistory, CurationReportHistoryDAO>
	implements CurationReportHistoryCrudInterface {

	@Inject
	CurationReportHistoryService curationReportHistoryService;

	@Override
	@PostConstruct
	protected void init() {
		setService(curationReportHistoryService);
	}

}
