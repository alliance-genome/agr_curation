package org.alliancegenome.curation_api.dao.curationreports;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;

@ApplicationScoped
public class CurationReportGroupDAO extends BaseSQLDAO<CurationReportGroup> {
	protected CurationReportGroupDAO() {
		super(CurationReportGroup.class);
	}
}
