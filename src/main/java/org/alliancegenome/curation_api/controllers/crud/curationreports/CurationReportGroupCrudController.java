package org.alliancegenome.curation_api.controllers.crud.curationreports;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportGroupDAO;
import org.alliancegenome.curation_api.interfaces.curationreports.CurationReportGroupCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;
import org.alliancegenome.curation_api.services.curationreports.CurationReportGroupService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CurationReportGroupCrudController extends BaseEntityCrudController<CurationReportGroupService, CurationReportGroup, CurationReportGroupDAO> implements CurationReportGroupCrudInterface {

	@Inject
	CurationReportGroupService curationReportGroupService;

	@Override
	@PostConstruct
	protected void init() {
		setService(curationReportGroupService);
	}

}
