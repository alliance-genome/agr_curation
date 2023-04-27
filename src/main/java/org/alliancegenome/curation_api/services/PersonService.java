package org.alliancegenome.curation_api.services;


import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.PersonValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;


@RequestScoped
public class PersonService extends BaseEntityCrudService<Person, PersonDAO> {

	@Inject
	PersonDAO personDAO;
	@Inject
	PersonValidator personValidator;

	Date personRequest = null;
	HashMap<String, Person> personCacheMap = new HashMap<>();
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(personDAO);
	}

	@Transactional
	public Person fetchByUniqueIdOrCreate(String uniqueId) {
		Person person = null;
		if(personRequest != null) {
			if(personCacheMap.containsKey(uniqueId)) {
				person = personCacheMap.get(uniqueId);
			} else {
				Log.debug("Person not cached, caching uniqueId: (" + uniqueId + ")");
				person = findPersonByUniqueIdOrCreateDB(uniqueId);
				personCacheMap.put(uniqueId, person);
			}
		} else {
			person = findPersonByUniqueIdOrCreateDB(uniqueId);
			personRequest = new Date();
		}
		return person;
	}
	
	private Person findPersonByUniqueIdOrCreateDB(String uniqueId) {
		SearchResponse<Person> personResponse = personDAO.findByField("uniqueId", uniqueId);
		if (personResponse != null && personResponse.getResults().size() > 0) {
			return personResponse.getSingleResult();
		}
		Person person = new Person();
		person.setUniqueId(uniqueId);
		return personDAO.persist(person);
	}

	public Person findPersonByEmail(String email) {
		SearchResponse<Person> resp = personDAO.findByField("email", email);
		if (resp != null && resp.getTotalResults() == 1) {
			return resp.getSingleResult();
		}
		return null;
	}

	@Override
	@Transactional
	public ObjectResponse<Person> update(Person uiEntity) {
		Person dbEntity = personValidator.validatePerson(uiEntity);
		return new ObjectResponse<>(personDAO.persist(dbEntity));
	}
	
	public Person findLoggedInPersonByOktaEmail(String email) {
		SearchResponse<Person> resp = personDAO.findByField("oktaEmail", email);
		if (resp != null && resp.getTotalResults() == 1) {
			return resp.getSingleResult();
		}

		return null;
	}

	@Transactional
	public Person regenApiToken() {
		Person user = personDAO.find(authenticatedPerson.getId());
		user.setApiToken(UUID.randomUUID().toString());
		return user;
	}
}
