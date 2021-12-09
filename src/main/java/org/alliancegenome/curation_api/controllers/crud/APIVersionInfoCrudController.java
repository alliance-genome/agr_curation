package org.alliancegenome.curation_api.controllers.crud;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.interfaces.APIVersionCrudInterface;
import org.alliancegenome.curation_api.model.output.APIVersionInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class APIVersionInfoCrudController implements APIVersionCrudInterface {

    @ConfigProperty(name = "quarkus.application.version")
    String version;
    
    @ConfigProperty(name = "quarkus.application.name")
    String name;
    
    @Override
    public APIVersionInfo get() {
        APIVersionInfo info = new APIVersionInfo();
        info.setVersion(version);
        info.setName(name);
        return info;
    }

}
