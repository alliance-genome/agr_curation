package org.alliancegenome.curation_api.services.helpers.persons;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

@RequestScoped
public class LoggedInPersonUniqueIdHelper {
	public String createLoggedInPersonUniqueId(LoggedInPerson loggedInPerson) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(loggedInPerson.getFirstName());
		uniqueId.add(loggedInPerson.getLastName());
		uniqueId.add(loggedInPerson.getOktaEmail());

		return uniqueId.getUniqueId();
	}
}
