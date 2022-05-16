package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class PersonDAO extends BaseSQLDAO<Person> {

    protected PersonDAO() {
        super(Person.class);
    }

}
