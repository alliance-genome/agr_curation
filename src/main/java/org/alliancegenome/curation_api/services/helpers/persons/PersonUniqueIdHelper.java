package org.alliancegenome.curation_api.services.helpers.persons;

import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PersonUniqueIdHelper {
	public String createLoggedInPersonUniqueId(Person loggedInPerson) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(loggedInPerson.getFirstName());
		uniqueId.add(loggedInPerson.getLastName());
		uniqueId.add(loggedInPerson.getOktaEmail());

		return uniqueId.getUniqueId();
	}
}
