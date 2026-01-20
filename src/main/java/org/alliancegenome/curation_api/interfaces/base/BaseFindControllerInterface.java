package org.alliancegenome.curation_api.interfaces.base;

import java.util.HashMap;

import org.alliancegenome.curation_api.model.Null;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseFindControllerInterface<E extends AuditedObject> {

	@POST
	@Path("/findForPublic")
	@Tag(name = "Public Web API Database Searching Endpoints")
	@RequestBody(
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	Response findForPublic(
		@DefaultValue("0") @QueryParam("page") Integer page,
		@DefaultValue("10") @QueryParam("limit") Integer limit,
		@DefaultValue("ForPublic") @QueryParam("view") String view,
		@RequestBody HashMap<String, Object> params);

	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.FieldsAndLists.class)
	@RequestBody(
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	SearchResponse<E> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
