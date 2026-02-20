package org.alliancegenome.curation_api.interfaces.document;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.model.document.es.GOSearchResultDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/goterm/document")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GODocumentInterface {

	@POST
	@Path("/searchresult")
	@JsonView(CurationView.GOSearchResultDocument.class)
	SearchResponse<GOSearchResultDocument> findSearchResult(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@GET
	@Path("/ids")
	SearchResponse<Long> getAllIds();

	@POST
	@Path("/byids")
	@JsonView(CurationView.GOSearchResultDocument.class)
	SearchResponse<GOSearchResultDocument> findByIds(@RequestBody List<Long> ids);

}