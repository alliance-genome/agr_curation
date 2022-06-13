package org.alliancegenome.curation_api.dao.curationreports;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;

@ApplicationScoped
public class CurationReportHistoryDAO extends BaseSQLDAO<CurationReportHistory> {
	protected CurationReportHistoryDAO() {
		super(CurationReportHistory.class);
	}
}
