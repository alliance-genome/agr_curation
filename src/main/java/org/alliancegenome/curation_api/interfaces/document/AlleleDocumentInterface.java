package org.alliancegenome.curation_api.interfaces.document;

import java.util.List;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
@Path("/allele/document")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDocumentInterface {

	@POST
	@Path("/ids")
	@JsonView(CurationView.AlleleSummaryDocument.class)
	SearchResponse<Long> getAllIds();

	@POST
	@Path("/summary/byids")
	@JsonView(CurationView.AlleleSummaryDocument.class)
	SearchResponse<AlleleSummaryDocument> findSummaryByIds(@RequestBody List<Long> ids);

}
