package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AffectedGenomicModelMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/agm")
@Tag(name = "CRUD - Affected Genomic Models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelCrudInterface extends BaseCurieCrudInterface<AffectedGenomicModel> {

    @Override
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<AffectedGenomicModel> get(@PathParam("curie") String curie);
    
    @POST @Secured
    @Path("/bulk/agmfile")
    public APIResponse updateAGMs(AffectedGenomicModelMetaDataFmsDTO agmData);
}
