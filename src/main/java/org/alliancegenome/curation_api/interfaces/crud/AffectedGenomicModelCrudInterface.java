package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/agm")
@Tag(name = "CRUD - Affected Genomic Models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelCrudInterface extends BaseCurieCrudInterface<AffectedGenomicModel> {

    @POST
    @Path("/bulk/agmfile")
    public String updateAGMs(AffectedGenomicModelMetaDataDTO agmData);
}
