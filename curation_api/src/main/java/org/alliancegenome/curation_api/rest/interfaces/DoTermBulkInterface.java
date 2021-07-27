package org.alliancegenome.curation_api.rest.interfaces;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.alliancegenome.curation_api.model.dto.xml.RDF;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/doterm/owl")
@Tag(name = "Disease Ontology")
@Produces(MediaType.APPLICATION_JSON)
public interface DoTermBulkInterface {
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	public Boolean updateDoTerms(@Context UriInfo uriInfo, @RequestBody RDF rdf);
	
}