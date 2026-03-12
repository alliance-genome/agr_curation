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
	@Operation(summary = "Find entities for public API", description = "Query the relational database (PostgreSQL via JPA/Hibernate) for entities using exact field-level filters. "
		+ "Returns a public-facing view of the data controlled by the `view` parameter.\n\n"
		+ "## Request Body\n\n"
		+ "A flat JSON object mapping field names to their expected values. All fields are matched using exact equality.\n\n"
		+ "### Field Names\n\n"
		+ "Field names correspond to JPA entity properties. Use dot notation to traverse nested relationships "
		+ "(e.g. `vocabulary.vocabularyLabel`, `diseaseAnnotationSubject.taxon.curie`). "
		+ "Collection fields in the traversal path are automatically joined.\n\n"
		+ "### Supported Value Types\n\n"
		+ "- **String**: Exact string equality\n"
		+ "- **Integer/Long**: Exact numeric equality\n"
		+ "- **Boolean**: Exact boolean equality (true/false)\n"
		+ "- **Array**: Exact collection match — entity collection must contain all listed values and be the same size\n"
		+ "- **null**: Checks that the collection field is empty (has no elements)\n\n"
		+ "### Reserved Keys\n\n"
		+ "- **debug** (optional): Set to the string `\"true\"` to include the generated HQL query in the response `dbQuery` field. Default false.\n"
		+ "- **query_operator** (optional): Set to `\"or\"` to OR fields together instead of the default AND.\n\n"
		+ "### Count-Only Mode\n\n"
		+ "When `page=0` and `limit=0`, only `totalResults` is returned without fetching entity records. "
		+ "When `limit > 0`, the `results` array is returned but `totalResults` is not populated.\n\n"
		+ "Results are always sorted ascending by entity primary key.\n\n"
		+ "For full documentation see [FIND.md](https://github.com/alliance-genome/agr_curation/blob/alpha/FIND.md)")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "Paginated results containing: results (list of matching entities), returnedRecords (page size), "
				+ "and optionally totalResults (only in count-only mode with page=0 and limit=0) "
				+ "and dbQuery (generated HQL query when debug is true)")
	)
	Response findForPublic(
		@Parameter(description = "Zero-based page number of results to return. Set page=0 with limit=0 for count-only mode.") @DefaultValue("0") @QueryParam("page") Integer page,
		@Parameter(description = "Number of results per page. Set to 0 with page=0 for count-only mode.") @DefaultValue("10") @QueryParam("limit") Integer limit,
		@Parameter(description = "JSON view class name controlling response serialization (e.g. ForPublic, FieldsOnly). Defaults to ForPublic if not recognized.") @DefaultValue("ForPublic") @QueryParam("view") String view,
		@RequestBody(description = "Flat map of field names to exact-match filter values. Supports dot notation for nested properties. "
			+ "Reserved keys: 'debug' (set to 'true' for query output), 'query_operator' (set to 'or' to OR fields).",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON,
				examples = {
					@ExampleObject(
						name = "Basic find",
						summary = "Find by nested field",
						value = "{\n"
							+ "  \"vocabulary.vocabularyLabel\": \"disease_qualifier\"\n"
							+ "}"
					),
					@ExampleObject(
						name = "Find with debug",
						summary = "Find with debug enabled to see the generated HQL query",
						value = "{\n"
							+ "  \"vocabulary.vocabularyLabel\": \"disease_qualifier\",\n"
							+ "  \"debug\": \"true\"\n"
							+ "}"
					),
					@ExampleObject(
						name = "Multiple fields with OR",
						summary = "Find where name OR symbol matches using query_operator",
						value = "{\n"
							+ "  \"name\": \"pax6\",\n"
							+ "  \"symbol\": \"pax6\",\n"
							+ "  \"query_operator\": \"or\"\n"
							+ "}"
					),
					@ExampleObject(
						name = "Boolean filter",
						summary = "Find non-obsolete, non-internal entities",
						value = "{\n"
							+ "  \"obsolete\": false,\n"
							+ "  \"internal\": false\n"
							+ "}"
					)
				}
			)
		) HashMap<String, Object> params);

	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.FieldsAndLists.class)
	@Operation(summary = "Find entities via database query", description = "Query the relational database (PostgreSQL via JPA/Hibernate) for entities using exact field-level filters. "
		+ "Returns the full curation view of the data.\n\n"
		+ "## Request Body\n\n"
		+ "A flat JSON object mapping field names to their expected values. All fields are matched using exact equality.\n\n"
		+ "### Field Names\n\n"
		+ "Field names correspond to JPA entity properties. Use dot notation to traverse nested relationships "
		+ "(e.g. `vocabulary.vocabularyLabel`, `diseaseAnnotationSubject.taxon.curie`). "
		+ "Collection fields in the traversal path are automatically joined.\n\n"
		+ "### Supported Value Types\n\n"
		+ "- **String**: Exact string equality\n"
		+ "- **Integer/Long**: Exact numeric equality\n"
		+ "- **Boolean**: Exact boolean equality (true/false)\n"
		+ "- **Array**: Exact collection match — entity collection must contain all listed values and be the same size\n"
		+ "- **null**: Checks that the collection field is empty (has no elements)\n\n"
		+ "### Reserved Keys\n\n"
		+ "- **debug** (optional): Set to the string `\"true\"` to include the generated HQL query in the response `dbQuery` field. Default false.\n"
		+ "- **query_operator** (optional): Set to `\"or\"` to OR fields together instead of the default AND.\n\n"
		+ "### Count-Only Mode\n\n"
		+ "When `page=0` and `limit=0`, only `totalResults` is returned without fetching entity records. "
		+ "When `limit > 0`, the `results` array is returned but `totalResults` is not populated.\n\n"
		+ "Results are always sorted ascending by entity primary key.\n\n"
		+ "For full documentation see [FIND.md](https://github.com/alliance-genome/agr_curation/blob/alpha/FIND.md)")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "Paginated results containing: results (list of matching entities), returnedRecords (page size), "
				+ "and optionally totalResults (only in count-only mode with page=0 and limit=0) "
				+ "and dbQuery (generated HQL query when debug is true)")
	)
	SearchResponse<E> find(
		@Parameter(description = "Zero-based page number of results to return. Set page=0 with limit=0 for count-only mode.") @DefaultValue("0") @QueryParam("page") Integer page,
		@Parameter(description = "Number of results per page. Set to 0 with page=0 for count-only mode.") @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody(description = "Flat map of field names to exact-match filter values. Supports dot notation for nested properties. "
			+ "Reserved keys: 'debug' (set to 'true' for query output), 'query_operator' (set to 'or' to OR fields).",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON,
				examples = {
					@ExampleObject(
						name = "Basic find",
						summary = "Find by nested field",
						value = "{\n"
							+ "  \"vocabulary.vocabularyLabel\": \"disease_qualifier\"\n"
							+ "}"
					),
					@ExampleObject(
						name = "Find with debug",
						summary = "Find with debug enabled to see the generated HQL query",
						value = "{\n"
							+ "  \"vocabulary.vocabularyLabel\": \"disease_qualifier\",\n"
							+ "  \"debug\": \"true\"\n"
							+ "}"
					),
					@ExampleObject(
						name = "Multiple fields with OR",
						summary = "Find where name OR symbol matches using query_operator",
						value = "{\n"
							+ "  \"name\": \"pax6\",\n"
							+ "  \"symbol\": \"pax6\",\n"
							+ "  \"query_operator\": \"or\"\n"
							+ "}"
					),
					@ExampleObject(
						name = "Boolean filter",
						summary = "Find non-obsolete, non-internal entities",
						value = "{\n"
							+ "  \"obsolete\": false,\n"
							+ "  \"internal\": false\n"
							+ "}"
					)
				}
			)
		) HashMap<String, Object> params);

}
