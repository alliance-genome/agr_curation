package org.alliancegenome.curation_api.interfaces.base;

import java.util.HashMap;

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

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseSearchControllerInterface<E> {

	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.FieldsAndLists.class })
	@Operation(summary = "Search entities via OpenSearch", description = "Search for entities using OpenSearch full-text search with structured filtering, sorting, and aggregation.\n\n"
		+ "## Search Filters (required)\n\n"
		+ "The `searchFilters` object contains named filter groups. Filter group names are arbitrary (e.g. 'nameFilter', 'obsoleteFilter'). "
		+ "By default, filter groups are AND'ed together. Set `searchFilterOperator` to 'OR' to OR them instead.\n\n"
		+ "Each filter group contains field entries keyed by field name. Fields use dot notation for nested properties "
		+ "(e.g. 'diseaseAnnotationSubject.taxon.curie'). Fields within a single filter group are OR'ed together.\n\n"
		+ "### Field Options\n\n"
		+ "- **queryString** (required): The search text. Will be tokenized and matched.\n"
		+ "- **tokenOperator** (optional): 'AND' (default) or 'OR' — controls how tokens within the queryString are matched.\n"
		+ "- **queryType** (optional): Set to 'matchQuery' for an Elasticsearch match query. Default uses simpleQueryString.\n"
		+ "- **useKeywordFields** (optional): If true, searches the `_keyword` variant of the field for exact matching. Default false.\n"
		+ "- **nonNullFields** (optional): List of fields that must be non-null within this filter group.\n"
		+ "- **nullFields** (optional): List of fields that must be null within this filter group.\n\n"
		+ "## Sort Orders (optional)\n\n"
		+ "Array of `{field, order}` objects. `field` is the field name (supports nested dot notation, uses `_keyword` suffix internally). "
		+ "`order` is 1 for ascending, -1 for descending.\n\n"
		+ "## Aggregations (optional)\n\n"
		+ "Array of field names to compute faceted counts for. Results appear in the response `aggregations` object keyed by field name.\n\n"
		+ "## Non Null Fields Table (optional)\n\n"
		+ "Array of field names that must be non-null across all results (applied globally, not per filter group).\n\n"
		+ "## Debug (optional)\n\n"
		+ "Set to 'true' to include the generated OpenSearch query and duration statistics in the response. Default false.\n\n"
		+ "For full documentation see [SEARCH.md](https://github.com/alliance-genome/agr_curation/blob/alpha/SEARCH.md)")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "Paginated search results containing: results (list of matching entities), totalResults (total match count), "
				+ "returnedRecords (page size), aggregations (faceted counts by field), and optionally esQuery (generated OpenSearch query when debug is true)")
	)
	SearchResponse<E> search(
		@Parameter(description = "Zero-based page number of results to return") @DefaultValue("0") @QueryParam("page") Integer page,
		@Parameter(description = "Number of results per page") @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody(description = "Search request payload",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON,
				examples = @ExampleObject(
					name = "Search example",
					summary = "Search with filters, sorting, and aggregations",
					value = "{\n"
						+ "	 \"searchFilters\": {\n"
						+ "	   \"nameFilter\": {\n"
						+ "		 \"name\": {\n"
						+ "		   \"queryString\": \"pax6 pax7\",\n"
						+ "		   \"tokenOperator\": \"OR\",\n"
						+ "		   \"queryType\": \"matchQuery\"\n"
						+ "		 }\n"
						+ "	   },\n"
						+ "	   \"obsoleteFilter\": {\n"
						+ "		 \"obsolete\": {\n"
						+ "		   \"queryString\": \"false\",\n"
						+ "		   \"tokenOperator\": \"AND\"\n"
						+ "		 },\n"
						+ "		 \"internal\": {\n"
						+ "		   \"queryString\": \"false\",\n"
						+ "		   \"tokenOperator\": \"AND\"\n"
						+ "		 }\n"
						+ "	   }\n"
						+ "	 },\n"
						+ "	 \"searchFilterOperator\": \"OR\",\n"
						+ "	 \"sortOrders\": [\n"
						+ "	   {\n"
						+ "		 \"field\": \"diseaseAnnotationSubject.symbol\",\n"
						+ "		 \"order\": 1\n"
						+ "	   }\n"
						+ "	 ],\n"
						+ "	 \"aggregations\": [\"secondaryDataProvider.sourceOrganization.abbreviation\"],\n"
						+ "	 \"nonNullFieldsTable\": [],\n"
						+ "	 \"debug\": \"true\"\n"
						+ "}"
				)
			)
		) HashMap<String, Object> params);

}
