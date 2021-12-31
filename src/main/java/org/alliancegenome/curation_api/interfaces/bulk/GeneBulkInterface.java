package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/gene")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneBulkInterface {
    
    @POST
    @Path("/submit")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String updateGenes(MultipartFormDataInput input);

}