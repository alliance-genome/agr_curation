package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.SearchResponse;

@RequestScoped
public class PersonService extends BaseCrudService<Person, PersonDAO> {

    @Inject
    PersonDAO personDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(personDAO);
    }
    
    @Transactional
    public Person fetchByUniqueIdOrCreate(String uniqueId) {
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
}
