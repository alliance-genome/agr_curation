package org.alliancegenome.curation_api.dao.curationreports;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CurationReportHistoryDAO extends BaseSQLDAO<CurationReportHistory> {
	protected CurationReportHistoryDAO() {
		super(CurationReportHistory.class);
	}
}
