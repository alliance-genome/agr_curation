package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.alliancegenome.curation_api.model.dto.xml.RDF;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/doterm/bulk")
@Tag(name = "Disease Ontology")
@Produces(MediaType.APPLICATION_JSON)
public interface DoTermBulkRESTInterface {
	
	@POST
	@Path("/owl")
	@Consumes(MediaType.APPLICATION_XML)
	public Boolean updateDoTerms(@Context UriInfo uriInfo, @RequestBody RDF rdf);
	
}