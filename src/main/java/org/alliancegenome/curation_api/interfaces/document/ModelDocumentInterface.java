package org.alliancegenome.curation_api.interfaces.document;

import java.util.List;

import org.alliancegenome.curation_api.model.document.es.ModelSearchResultDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * SCRUM-5124 - public REST endpoints feeding the site index with AGM (Affected Genomic Model)
 * search-result documents. Mirrors the shape of {@code AlleleDocumentInterface}: callers first
 * fetch the full ID set via {@code /ids}, then hydrate chunks of those IDs into documents via
 * {@code /summary/byids}.
 */
@Path("/model/document")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
public interface ModelDocumentInterface {

	@GET
	@Path("/ids")
	@JsonView(CurationView.ModelSearchResultDocument.class)
	SearchResponse<Long> getAllIds();

	@POST
	@Path("/summary/byids")
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(CurationView.ModelSearchResultDocument.class)
	SearchResponse<ModelSearchResultDocument> findSummaryByIds(@RequestBody List<Long> ids);
}