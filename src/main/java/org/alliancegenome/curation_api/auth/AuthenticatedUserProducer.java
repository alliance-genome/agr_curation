package org.alliancegenome.curation_api.auth;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import org.alliancegenome.curation_api.model.entities.Person;

@RequestScoped
public class AuthenticatedUserProducer {

    @Produces
    @RequestScoped
    @AuthenticatedUser
    Person authenticatedPerson = new Person();

    public void handleAuthenticationEvent(@Observes @AuthenticatedUser Person authenticatedPerson) {
        this.authenticatedPerson = authenticatedPerson;
    }
}
