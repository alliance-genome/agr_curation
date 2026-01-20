package org.alliancegenome.curation_api.interfaces.document;

import org.alliancegenome.curation_api.model.document.es.GeneExpressionRibbonSummaryDocument;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/gene-expression-ribbon-summary")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionRibbonDocumentInterface {
	
	@POST
	@Path("")
	@JsonView(CurationView.ForPublic.class)
	GeneExpressionRibbonSummaryDocument findDocument();
	
}
