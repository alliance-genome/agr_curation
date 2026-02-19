package org.alliancegenome.curation_api.interfaces.base;

import java.util.HashMap;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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
	@Operation(summary = "Find entities for public API", description = "Query the relational database for entities using exact field-level filters. Returns a public-facing view of the data.\n\n"
		+ "## Fields (required)\n\n"
		+ "The request body is a flat map of field names to values. All fields are exact matches and are AND'ed together. "
		+ "Fields use dot notation for nested properties (e.g. 'vocabulary.vocabularyLabel').\n\n"
		+ "## Debug (optional)\n\n"
		+ "Set `debug` to 'true' to include the generated JQL database query in the response `dbQuery` field. Default false.\n\n"
		+ "For full documentation see [FIND.md](https://github.com/alliance-genome/agr_curation/blob/alpha/FIND.md)")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "Paginated results containing: results (list of matching entities), totalResults (total match count), "
				+ "returnedRecords (page size), and optionally dbQuery (generated JQL query when debug is true)")
	)
	Response findForPublic(
		@Parameter(description = "Zero-based page number of results to return") @DefaultValue("0") @QueryParam("page") Integer page,
		@Parameter(description = "Number of results per page") @DefaultValue("10") @QueryParam("limit") Integer limit,
		@Parameter(description = "Response view format") @DefaultValue("ForPublic") @QueryParam("view") String view,
		@RequestBody(description = "Map of field names to exact-match filter values",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON,
				examples = @ExampleObject(
					name = "Find example",
					summary = "Find by field with debug enabled",
					value = "{\n"
						+ "  \"vocabulary.vocabularyLabel\": \"disease_qualifier\",\n"
						+ "  \"debug\": \"true\"\n"
						+ "}"
				)
			)
		) HashMap<String, Object> params);

	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.FieldsAndLists.class)
	@Operation(summary = "Find entities via database query", description = "Query the relational database for entities using exact field-level filters. Returns the full curation view of the data.\n\n"
		+ "## Fields (required)\n\n"
		+ "The request body is a flat map of field names to values. All fields are exact matches and are AND'ed together. "
		+ "Fields use dot notation for nested properties (e.g. 'vocabulary.vocabularyLabel').\n\n"
		+ "## Debug (optional)\n\n"
		+ "Set `debug` to 'true' to include the generated JQL database query in the response `dbQuery` field. Default false.\n\n"
		+ "For full documentation see [FIND.md](https://github.com/alliance-genome/agr_curation/blob/alpha/FIND.md)")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "Paginated results containing: results (list of matching entities), totalResults (total match count), "
				+ "returnedRecords (page size), and optionally dbQuery (generated JQL query when debug is true)")
	)
	SearchResponse<E> find(
		@Parameter(description = "Zero-based page number of results to return") @DefaultValue("0") @QueryParam("page") Integer page,
		@Parameter(description = "Number of results per page") @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody(description = "Map of field names to exact-match filter values",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON,
				examples = @ExampleObject(
					name = "Find example",
					summary = "Find by field with debug enabled",
					value = "{\n"
						+ "  \"vocabulary.vocabularyLabel\": \"disease_qualifier\",\n"
						+ "  \"debug\": \"true\"\n"
						+ "}"
				)
			)
		) HashMap<String, Object> params);

}
