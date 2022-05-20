package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.fasterxml.jackson.annotation.JsonView;


@Path("/loggedinperson")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface LoggedInPersonCrudInterface {

    @GET
    @Path("/")
    @JsonView(View.FieldsOnly.class)
    public LoggedInPerson getLoggedInPerson();
    
    @POST
    @Path("/savesettings")
    @Operation(hidden=true)
    public void saveSettings(@RequestBody HashMap<String, Object> settings);
    
}
