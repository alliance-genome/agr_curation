package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.alliancegenome.curation_api.model.ingest.xml.dto.RDF;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/mpterm/bulk")
@Tag(name = "Ontology - MP")
@Produces(MediaType.APPLICATION_JSON)
public interface MpTermBulkRESTInterface {
    
    @POST
    @Path("/owl")
    @Consumes(MediaType.APPLICATION_XML)
    public Boolean updateMpTerms(@Context UriInfo uriInfo, @RequestBody RDF rdf);
    
}