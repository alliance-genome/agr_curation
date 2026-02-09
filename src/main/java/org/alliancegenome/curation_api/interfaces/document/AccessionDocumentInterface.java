package org.alliancegenome.curation_api.interfaces.document;

import org.alliancegenome.curation_api.model.document.es.AccessionSummaryDocument;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/accession/document")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AccessionDocumentInterface {

	@GET
	@Path("/summary")
	@JsonView(CurationView.AccessionSummaryDocument.class)
	AccessionSummaryDocument getAccessionSummary();
}
