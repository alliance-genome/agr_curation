package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class LoggedInPersonDAO extends BaseSQLDAO<LoggedInPerson> {

    protected LoggedInPersonDAO() {
        super(LoggedInPerson.class);
    }
    
    public LoggedInPerson findLoggedInPersonByOktaEmail(String email) {
        SearchResponse<LoggedInPerson> resp = findByField("oktaEmail", email);
        if (resp != null && resp.getTotalResults() == 1) {
            return resp.getSingleResult();
        }
        
        return null;
    }

}
