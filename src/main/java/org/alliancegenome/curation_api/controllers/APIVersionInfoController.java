package org.alliancegenome.curation_api.controllers;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.interfaces.APIVersionInterface;
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
    
    
    @Override
    public APIVersionInfo get() {
        APIVersionInfo info = new APIVersionInfo();
        info.setVersion(version);
        info.setName(name);
        info.setLinkMLVersion(linkMLVersion);
        info.setLinkMLClasses(linkMLClasses);
        return info;
    }

}
