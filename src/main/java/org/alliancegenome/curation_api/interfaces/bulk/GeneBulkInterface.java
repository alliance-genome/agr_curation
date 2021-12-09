package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.ingest.json.dto.GeneMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gene/bulk")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneBulkInterface {
    
    @POST
    @Path("/bgifile")
    public String updateBGI(
        GeneMetaDataDTO geneData, 
        @DefaultValue("true") 
        @QueryParam("async") boolean async);

}