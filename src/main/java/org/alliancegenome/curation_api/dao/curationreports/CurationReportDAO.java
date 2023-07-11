package org.alliancegenome.curation_api.dao.curationreports;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CurationReportDAO extends BaseSQLDAO<CurationReport> {
	protected CurationReportDAO() {
		super(CurationReport.class);
	}
}
