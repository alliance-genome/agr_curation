package org.alliancegenome.curation_api.services.helpers.persons;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;

@RequestScoped
public class LoggedInPersonUniqueIdHelper {
    public String createLoggedInPersonUniqueId (LoggedInPerson loggedInPerson) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(loggedInPerson.getFirstName());
        curie.add(loggedInPerson.getLastName());
        curie.add(loggedInPerson.getOktaEmail());
        
        return curie.getCurie();
    }
}
