package org.alliancegenome.curation_api.controllers;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.interfaces.APIVersionInterface;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.output.APIVersionInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class APIVersionInfoController implements APIVersionInterface {

    @ConfigProperty(name = "quarkus.application.version")
    String version;
    
    @ConfigProperty(name = "quarkus.application.name")
    String name;
    
    @ConfigProperty(name = "linkML.version")
    String linkMLVersion;
    
    @ConfigProperty(name = "linkML.classes")
    List<String> linkMLClasses;
    
    @ConfigProperty(name = "quarkus.hibernate-search-orm.elasticsearch.hosts")
    String es_host;
    
    @ConfigProperty(name = "NET")
    String env;
    

    @Override
    public APIVersionInfo get() {
        APIVersionInfo info = new APIVersionInfo();
        info.setVersion(version);
        info.setName(name);
        info.setLinkMLVersion(linkMLVersion);
        info.setLinkMLClasses(linkMLClasses);
        info.setEsHost(es_host);
        info.setEnv(env);
        return info;
    }

}
