package org.alliancegenome.curation_api.interfaces.document;

import java.util.HashMap;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/geneexpression/document")
@Tag(name = "Public Document Endpoints")
@Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
@Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
public interface GeneExpressionDocumentInterface {

	/**
	 *
	 * @param page
	 * @param limit
	 * @param params
	 * @return The gene expression annotations with the cross references for MGI and WB for the indexer
	 */
	@POST
	@Path("/annotations")
	Response getAnnotationsForIndexing(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);
}
