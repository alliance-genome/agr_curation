package org.alliancegenome.curation_api.interfaces.person;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.entities.PersonSetting;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

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
