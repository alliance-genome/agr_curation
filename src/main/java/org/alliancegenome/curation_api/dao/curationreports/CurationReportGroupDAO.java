package org.alliancegenome.curation_api.dao.curationreports;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CurationReportGroupDAO extends BaseSQLDAO<CurationReportGroup> {
	protected CurationReportGroupDAO() {
		super(CurationReportGroup.class);
	}
}
