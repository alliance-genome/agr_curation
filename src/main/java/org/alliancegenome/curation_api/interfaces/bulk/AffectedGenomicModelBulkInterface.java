package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/agm/bulk")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelBulkInterface {
    
    @POST
    @Path("/agmfile")
    public String updateAGMs(
            AffectedGenomicModelMetaDataDTO agmData,
            @DefaultValue("true") 
            @QueryParam("async") boolean async);

}