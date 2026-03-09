package org.alliancegenome.curation_api.interfaces.document;
import java.util.List;

import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/gene-expression")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionDocumentInterface {
	
	@POST
	@Path("/expression-documents")
	@JsonView(CurationView.GeneExpressionDocument.class)
	SearchResponse<GeneExpressionDocument> getConsolidateDocumentsForGenes(List<String> geneIds);

	@GET
	@Path("/geneIds")
	SearchResponse<String> getGeneIds();
}
