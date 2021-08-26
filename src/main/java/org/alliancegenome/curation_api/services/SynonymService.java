package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.model.entities.GeneSynonym;
import org.alliancegenome.curation_api.model.entities.Synonym;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@JBossLog
@ApplicationScoped
public class SynonymService extends BaseService<GeneSynonym, SynonymDAO> {

    @Inject
    SynonymDAO synonymDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(synonymDAO);
    }
    
}
