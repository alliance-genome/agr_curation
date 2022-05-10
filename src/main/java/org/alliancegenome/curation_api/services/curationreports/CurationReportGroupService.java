package org.alliancegenome.curation_api.services.curationreports;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportGroupDAO;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;

@RequestScoped
public class CurationReportGroupService extends BaseCrudService<CurationReportGroup, CurationReportGroupDAO> {
    
    @Inject
    CurationReportGroupDAO curationReportGroupDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(curationReportGroupDAO);
    }
}