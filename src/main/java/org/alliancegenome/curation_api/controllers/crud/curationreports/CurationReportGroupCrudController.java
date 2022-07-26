package org.alliancegenome.curation_api.controllers.crud.curationreports;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportGroupDAO;
import org.alliancegenome.curation_api.interfaces.curationreports.CurationReportGroupCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;
import org.alliancegenome.curation_api.services.curationreports.CurationReportGroupService;

@RequestScoped
public class CurationReportGroupCrudController extends BaseEntityCrudController<CurationReportGroupService, CurationReportGroup, CurationReportGroupDAO> implements CurationReportGroupCrudInterface {

	@Inject CurationReportGroupService curationReportGroupService;

	@Override
	@PostConstruct
	protected void init() {
		setService(curationReportGroupService);
	}

}
