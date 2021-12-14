package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/allele")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
public interface AlleleBulkInterface {

    @POST
    @Path("/submit_oldschema")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String updateOldAlleles(MultipartFormDataInput input);

    @POST
    @Path("/submit")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String updateAlleles(MultipartFormDataInput input);

}
