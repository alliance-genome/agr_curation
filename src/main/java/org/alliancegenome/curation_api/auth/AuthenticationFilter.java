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

        //log.info("AuthenticationFilter: filter: " + requestContext);

        // Get the Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        //log.info("Authorization Header: " + authorizationHeader);

        if (!isTokenBasedAuthentication(authorizationHeader)) {
            if(okta_url.get().equals("\"\"") || client_id.get().equals("\"\"") || client_secret.get().equals("\"\"") || api_token.get().equals("\"\"")) {
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
            } else {
                abortWithUnauthorized(requestContext);
            }
            return;
        }

        //      // Extract the token from the Authorization header
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        //log.info("Inbound token: " + token);

        try {
            validateToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(e.getLocalizedMessage());
            abortWithUnauthorized(requestContext);
        }

    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }
    //
    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response

        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
        //requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
    }

    private void validateToken(String token) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
        //log.debug("API Access Token: " + ConfigHelper.getApiAccessToken());
        //log.info("Validating Token: " + token);

        OktaTokenInterface oti = RestProxyFactory.createProxy(OktaTokenInterface.class, okta_url.get());

        String basic = "Basic " + Base64.getEncoder().encodeToString((client_id.get() + ":" + client_secret.get()).getBytes());

        OktaUserInfo info = oti.getUserInfo(basic, "access_token", token);

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
                userAuthenticatedEvent.fire(person);
            } else {
                userAuthenticatedEvent.fire(res.getResults().get(0));
            }

        } else {
            SearchResponse<Person> res = personDAO.findByField("apiToken", api_token.get());
            if(res != null && res.getResults().size() == 1) {
                userAuthenticatedEvent.fire(res.getResults().get(0));
            } else {
                log.warn("Authentication Unsuccessful: " + token);
                throw new Exception("Authentication Unsuccessful: " + token + " failed authentication");
            }
        }
    }
}