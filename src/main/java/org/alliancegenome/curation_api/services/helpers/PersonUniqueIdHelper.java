package org.alliancegenome.curation_api.services.helpers;

import org.alliancegenome.curation_api.model.entities.Person;

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
