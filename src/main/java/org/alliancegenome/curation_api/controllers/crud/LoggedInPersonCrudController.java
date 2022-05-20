package org.alliancegenome.curation_api.controllers.crud;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.LoggedInPersonDAO;
import org.alliancegenome.curation_api.interfaces.crud.LoggedInPersonCrudInterface;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.services.LoggedInPersonService;

@RequestScoped
public class LoggedInPersonCrudController extends BaseCrudController<LoggedInPersonService, LoggedInPerson, LoggedInPersonDAO> implements LoggedInPersonCrudInterface {

    @Inject LoggedInPersonService loggedInPersonService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(loggedInPersonService);
    }
    
    public void saveSettings(HashMap<String, Object> settings) {
        if (settings == null) settings = new HashMap<>();
        loggedInPersonService.saveSettings(settings);
    }
}
