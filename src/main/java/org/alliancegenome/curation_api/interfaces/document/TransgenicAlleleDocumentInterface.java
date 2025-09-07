package org.alliancegenome.curation_api.interfaces.document;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.HashMap;

import org.alliancegenome.curation_api.model.document.es.TransgenicAlleleDTO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/allele")
@Tag(name = "Public Document Endpoints")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface TransgenicAlleleDocumentInterface {

	@POST
	@Path("/transgenic-allele-documents")
	@JsonView(View.TransgenicAllelesDocumentView.class)
	SearchResponse<TransgenicAlleleDTO> findDocuments(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);
}
