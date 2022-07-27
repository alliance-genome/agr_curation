package org.alliancegenome.curation_api.interfaces.base;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "CRUD - Ontology - Bulk")
public interface BaseOntologyTermCrudInterface<E extends OntologyTerm> extends BaseCurieCrudInterface<E> {

	@POST
	@Path("/bulk/owl")
	@Consumes(MediaType.APPLICATION_XML)
	public String updateTerms(
			@DefaultValue("true") 
			@QueryParam("async") boolean async,
			@RequestBody String fullText);
	
	public void init();
}
