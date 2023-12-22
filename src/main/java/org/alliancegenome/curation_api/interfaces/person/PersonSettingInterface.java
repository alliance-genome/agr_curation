package org.alliancegenome.curation_api.interfaces.person;

import java.util.Map;

import org.alliancegenome.curation_api.model.entities.PersonSetting;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/personsettings")
@Tag(name = "User Setting Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PersonSettingInterface {

	@GET
	@Path("/{settingsKey}")
	@JsonView(View.PersonSettingView.class)
	public ObjectResponse<PersonSetting> getUserSetting(@PathParam("settingsKey") String settingsKey);

	@PUT
	@Path("/{settingsKey}")
	@JsonView(View.PersonSettingView.class)
	public ObjectResponse<PersonSetting> saveUserSetting(@PathParam("settingsKey") String settingsKey, @RequestBody Map<String, Object> settingsMap);

	@DELETE
	@Path("/{settingsKey}")
	@JsonView(View.PersonSettingView.class)
	public ObjectResponse<PersonSetting> deleteUserSetting(@PathParam("settingsKey") String settingsKey);

}
