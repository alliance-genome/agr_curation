package org.alliancegenome.curation_api.interfaces.document;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/ontologytermclosure/document")
@Tag(name = "Public Document Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OntologyTermClosureDocumentInterface {

	@POST
	@Path("/ids")
	SearchResponse<Long> getAllIds(
		@Parameter(description = "Ontology term type discriminator on both ends of the closure (e.g. DOTerm)") @QueryParam("ontologyTermType") String ontologyTermType,
		@Parameter(description = "Comma-separated relation types the closure must equal (e.g. is_a,part_of)") @QueryParam("relationTypes") String relationTypes);

	@POST
	@Path("/byids")
	@JsonView(CurationView.ForPublic.class)
	SearchResponse<OntologyTermClosure> findByIds(@RequestBody List<Long> ids);
}
