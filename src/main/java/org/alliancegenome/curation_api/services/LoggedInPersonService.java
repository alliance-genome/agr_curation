package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.LoggedInPersonDAO;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;

@RequestScoped
public class LoggedInPersonService extends BaseCrudService<LoggedInPerson, LoggedInPersonDAO> {

    @Inject
    LoggedInPersonDAO loggedInPersonDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(loggedInPersonDAO);
    }
    
}
