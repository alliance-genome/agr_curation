package org.alliancegenome.curation_api.services.curationreports;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;

@RequestScoped
public class CurationReportService extends BaseCrudService<CurationReport, CurationReportDAO> {
    
    @Inject
    CurationReportDAO curationReportDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(curationReportDAO);
    }
}