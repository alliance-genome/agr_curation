package org.alliancegenome.curation_api.interfaces.person;

import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/person")
@Tag(name = "Logged In Person Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PersonInterface {

	@POST
	@Path("/")
	@Operation(hidden = true)
	@JsonView(View.FieldsOnly.class)
	ObjectResponse<Person> create(Person entity);

	@GET
	@Path("/")
	@JsonView(View.PersonSettingView.class)
	Person getLoggedInPerson();

	@GET
	@Path("/regenapitoken")
	Person regenApiToken();

}
