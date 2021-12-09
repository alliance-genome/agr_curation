package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/allele/bulk")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleBulkInterface {
    
    @POST
    @Path("/allelefile")
    public String updateAlleles(
        AlleleMetaDataDTO alleleData,
        @DefaultValue("true") 
        @QueryParam("async") boolean async);

}
