package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.util.*;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;

import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.interfaces.okta.*;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.*;
import com.okta.sdk.resource.user.User;

import lombok.extern.jbosslog.JBossLog;
import si.mazi.rescu.RestProxyFactory;

@JBossLog
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    @AuthenticatedUser
    Event<Person> userAuthenticatedEvent;

    @Inject PersonDAO personDAO;

    @ConfigProperty(name = "okta.authentication")
    Instance<Boolean> okta_auth;

    @ConfigProperty(name = "okta.url")
    Instance<String> okta_url;

    @ConfigProperty(name = "okta.client.id")
    Instance<String> client_id;

    @ConfigProperty(name = "okta.client.secret")
    Instance<String> client_secret;

    @ConfigProperty(name = "okta.api.token")
    Instance<String> api_token;

    //private static final String REALM = "AGR";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Logic:
        // if okta_auth is off then we are in test mode
        // if okta_auth is on but no okta_creds we are in Test mode (Integration Testing)
        // if okta_auth is on and we have okta_creds validate(token), else fail

        if(okta_auth.get()) {
            if(!okta_url.get().equals("\"\"") && !client_id.get().equals("\"\"") && !client_secret.get().equals("\"\"") && !api_token.get().equals("\"\"")) {

                String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

                if (authorizationHeader == null || !authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
                    failAuthentication(requestContext);
                } else {
                    String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
    
                    Person person = validateToken(token);
                    if(person != null) {
                        userAuthenticatedEvent.fire(person);
                    } else {
                        failAuthentication(requestContext);
                    }
                }

            } else {
                // Test / Dev Mode
                loginDevUser();
            }
        } else {
            // Test / Dev Mode
            loginDevUser();
        }

    }
    
    private void failAuthentication(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
    }

    private void loginDevUser() {
        log.debug("OKTA Authentication Disabled using Test Dev User");
        SearchResponse<Person> res = personDAO.findPersonByEmail("test@alliancegenome.org");
        if(res == null) {
            Person person = new Person();
            person.setApiToken(UUID.randomUUID().toString());
            person.setEmail("test@alliancegenome.org");
            person.setFirstName("Local");
            person.setLastName("Dev User");
            personDAO.persist(person);
            userAuthenticatedEvent.fire(person);
        } else {
            userAuthenticatedEvent.fire(res.getResults().get(0));
        }
    }

    // Check Okta(token), Check DB ApiToken(token), else return null
    private Person validateToken(String token) {

        OktaTokenInterface oti = RestProxyFactory.createProxy(OktaTokenInterface.class, okta_url.get());

        String basic = "Basic " + Base64.getEncoder().encodeToString((client_id.get() + ":" + client_secret.get()).getBytes());

        OktaUserInfo info = oti.getUserInfo(basic, "access_token", token);

        if(info.getUid() == null || info.getUid().length() == 0) {
            return null;
        } else {
            Client client = Clients.builder()
                    .setOrgUrl(okta_url.get())
                    .setClientId(client_id.get())
                    .setClientCredentials(new TokenClientCredentials(api_token.get()))
                    .build();

            User user = client.getUser(info.getUid());

            if(user != null) {
                SearchResponse<Person> res = personDAO.findPersonByEmail(user.getProfile().getEmail());
                if(res == null) {
                    Person person = new Person();
                    person.setApiToken(UUID.randomUUID().toString());
                    person.setEmail(user.getProfile().getEmail());
                    person.setFirstName(user.getProfile().getFirstName());
                    person.setLastName(user.getProfile().getLastName());
                    personDAO.persist(person);
                    return person;
                } else {
                    return res.getResults().get(0);
                }
            } else {
                SearchResponse<Person> res = personDAO.findByField("apiToken", token);
                if(res != null && res.getResults().size() == 1) {
                    return res.getResults().get(0);
                }
            }
        }
        
        return null;
    }
}