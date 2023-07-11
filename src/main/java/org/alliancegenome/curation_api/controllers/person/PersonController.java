package org.alliancegenome.curation_api.controllers.person;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.interfaces.person.PersonInterface;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PersonController implements PersonInterface {

	@Inject
	@AuthenticatedUser
	Person authenticatedPerson;
	@Inject
	PersonService personService;

	public ObjectResponse<Person> create(Person person) {
		return personService.create(person);
	}

	@Override
	public Person getLoggedInPerson() {
		return authenticatedPerson;
	}

	@Override
	public Person regenApiToken() {
		return personService.regenApiToken();
	}
}
