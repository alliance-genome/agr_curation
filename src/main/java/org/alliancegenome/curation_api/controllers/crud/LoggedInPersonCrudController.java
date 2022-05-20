package org.alliancegenome.curation_api.controllers.crud;

import java.util.HashMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.interfaces.crud.LoggedInPersonCrudInterface;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.services.LoggedInPersonService;

@RequestScoped
public class LoggedInPersonCrudController implements LoggedInPersonCrudInterface {

    @Inject @AuthenticatedUser LoggedInPerson authenticatedPerson;
    @Inject LoggedInPersonService loggedInPersonService;
    
    public void saveSettings(HashMap<String, Object> settings) {
        if (settings == null) settings = new HashMap<>();
        loggedInPersonService.saveSettings(settings);
    }
    
    @Override
    public LoggedInPerson getLoggedInPerson() {
        return authenticatedPerson;
    }
}
