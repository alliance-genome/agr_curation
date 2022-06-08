package org.alliancegenome.curation_api.dao.curationreports;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;

@ApplicationScoped
public class CurationReportDAO extends BaseSQLDAO<CurationReport> {
	protected CurationReportDAO() {
		super(CurationReport.class);
	}
}
