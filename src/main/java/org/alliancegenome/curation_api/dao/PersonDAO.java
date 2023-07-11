package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Person;

@ApplicationScoped
public class PersonDAO extends BaseSQLDAO<Person> {

	protected PersonDAO() {
		super(Person.class);
	}

}
