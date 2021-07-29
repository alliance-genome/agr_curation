package org.alliancegenome.curation_api.rest.interfaces;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.alliancegenome.curation_api.model.dto.xml.RDF;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/mpterm/owl")
@Tag(name = "MP Ontology")
@Produces(MediaType.APPLICATION_JSON)
public interface MpTermBulkInterface {
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	public Boolean updateMpTerms(@Context UriInfo uriInfo, @RequestBody RDF rdf);
	
}