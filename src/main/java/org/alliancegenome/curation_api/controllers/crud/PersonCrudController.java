package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.interfaces.crud.PersonCrudInterface;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.services.PersonService;

@RequestScoped
public class PersonCrudController extends BaseCrudController<PersonService, Person, PersonDAO> implements PersonCrudInterface {

    @Inject PersonService personService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(personService);
    }
}
