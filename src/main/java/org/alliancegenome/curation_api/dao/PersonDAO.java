package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Person;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonDAO extends BaseSQLDAO<Person> {

	protected PersonDAO() {
		super(Person.class);
	}

}
