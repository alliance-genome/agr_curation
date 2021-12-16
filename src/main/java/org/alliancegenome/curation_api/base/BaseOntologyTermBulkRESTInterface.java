package org.alliancegenome.curation_api.base;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

public interface BaseOntologyTermBulkRESTInterface {

    @POST
    @Path("/owl")
    @Consumes(MediaType.APPLICATION_XML)
    public String updateTerms(
            @DefaultValue("true") 
            @QueryParam("async") boolean async,
            @RequestBody String fullText);
    
    public void init();
}
