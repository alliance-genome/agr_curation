package org.alliancegenome.curation_api.services;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.LoggedInPersonDAO;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.SearchResponse;

@RequestScoped
public class LoggedInPersonService extends BaseCrudService<LoggedInPerson, LoggedInPersonDAO> {

    @Inject
    LoggedInPersonDAO loggedInPersonDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(loggedInPersonDAO);
    }
    
    @Transactional
    public void saveSettings(HashMap<String, Object> settings) {
        LoggedInPerson user = loggedInPersonDAO.find(authenticatedPerson.getId());
        user.setUserSettings(settings);
    }
    
    public LoggedInPerson findLoggedInPersonByOktaEmail(String email) {
        SearchResponse<LoggedInPerson> resp = loggedInPersonDAO.findByField("oktaEmail", email);
        if (resp != null && resp.getTotalResults() == 1) {
            return resp.getSingleResult();
        }
        
        return null;
    }
    
}
