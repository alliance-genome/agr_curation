package org.alliancegenome.curation_api.interfaces.document;

import java.util.HashMap;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/allele/document")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDocumentInterface {

	@POST
	@Path("/summary")
	@JsonView(View.AlleleSummaryDocument.class)
	SearchResponse<AlleleSummaryDocument> findSummary(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);
	
	@POST
	@Path("/summary/cursor")
	@JsonView(View.AlleleSummaryDocument.class)
	@Operation(summary = "Find allele summaries using cursor-based pagination for optimal performance", 
			   description = "Use cursor-based pagination to efficiently navigate large datasets. Use the nextCursor from the previous response as the cursor parameter for the next page.")
	SearchResponse<AlleleSummaryDocument> findSummaryWithCursor(
		@DefaultValue("0") @QueryParam("page") Integer page, 
		@DefaultValue("10") @QueryParam("limit") Integer limit, 
		@Parameter(description = "Cursor for pagination - use nextCursor from previous response") @QueryParam("cursor") Long cursor, 
		@RequestBody HashMap<String, Object> params);

}