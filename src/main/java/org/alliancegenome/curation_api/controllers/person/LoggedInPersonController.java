package org.alliancegenome.curation_api.controllers.person;

import java.util.HashMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.interfaces.person.LoggedInPersonInterface;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.LoggedInPersonService;

@RequestScoped
public class LoggedInPersonController implements LoggedInPersonInterface {

    @Inject @AuthenticatedUser LoggedInPerson authenticatedPerson;
    @Inject LoggedInPersonService loggedInPersonService;
    
    public void saveSettings(HashMap<String, Object> settings) {
        if (settings == null) settings = new HashMap<>();
        loggedInPersonService.saveSettings(settings);
    }
    
    public ObjectResponse<LoggedInPerson> create(LoggedInPerson person) {
        return loggedInPersonService.create(person);
    }
    
    @Override
    public LoggedInPerson getLoggedInPerson() {
        return authenticatedPerson;
    }
}
