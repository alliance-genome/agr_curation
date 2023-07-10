package org.alliancegenome.curation_api.services.curationreports;

import org.alliancegenome.curation_api.dao.curationreports.CurationReportGroupDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CurationReportGroupService extends BaseEntityCrudService<CurationReportGroup, CurationReportGroupDAO> {

	@Inject
	CurationReportGroupDAO curationReportGroupDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(curationReportGroupDAO);
	}
}