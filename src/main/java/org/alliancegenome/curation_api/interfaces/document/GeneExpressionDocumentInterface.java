package org.alliancegenome.curation_api.interfaces.document;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.alliancegenome.curation_api.view.View.ExpressionDetailDocument;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/geneexpression/document")
@Tag(name = "Public Document Endpoints")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface GeneExpressionDocumentInterface {

	/**
	 * @param page
	 * @param limit
	 *
	 * @return The gene expression annotations with the cross references for MGI and WB for the indexer
	 */
	@POST
	@Path("/annotations")
	@JsonView({ExpressionDetailDocument.class, View.FieldsOnly.class})
	SearchResponse<ExpressionDetailDocument> getAnnotationsForIndexing(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit);
}
