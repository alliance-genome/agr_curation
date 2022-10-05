package org.alliancegenome.curation_api.interfaces.person;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;


@Path("/loggedinperson")
@Tag(name = "Logged In Person Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface LoggedInPersonInterface {

	@POST
	@Path("/")
	@Operation(hidden=true)
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<LoggedInPerson> create(LoggedInPerson entity);
	
	@GET
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	public LoggedInPerson getLoggedInPerson();
	
	@POST
	@Path("/savesettings")
	public void saveSettings(@RequestBody HashMap<String, Object> settings);
	
	@GET
	@Path("/regenapitoken")
	public LoggedInPerson regenApiToken();
	
}
