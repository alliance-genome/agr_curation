package org.alliancegenome.curation_api.interfaces;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.output.APIVersionInfo;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/version")
@Tag(name = "API Version")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface APIVersionInterface {

    @GET
    @Path("/")
    @JsonView(View.FieldsOnly.class)
    public APIVersionInfo get();
    
    @GET @Secured
    @Path("/user")
    @JsonView(View.FieldsOnly.class)
    public Person getAuthUser();
    
}
