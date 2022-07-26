package org.alliancegenome.curation_api.controllers.crud.curationreports;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportDAO;
import org.alliancegenome.curation_api.interfaces.curationreports.CurationReportCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.curationreports.CurationReportService;

@RequestScoped
public class CurationReportCrudController extends BaseEntityCrudController<CurationReportService, CurationReport, CurationReportDAO> implements CurationReportCrudInterface {

	@Inject CurationReportService curationReportService;

	@Override
	@PostConstruct
	protected void init() {
		setService(curationReportService);
	}

	@Override
	public ObjectResponse<CurationReport> restartReport(Long id) {
		return curationReportService.restartReport(id);
	}
}
