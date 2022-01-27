package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AffectedGenomicModelMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/agm")
@Tag(name = "CRUD - Affected Genomic Models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelCrudInterface extends BaseCurieCrudInterface<AffectedGenomicModel> {

    @JsonView(View.FieldsAndLists.class)
    @Override
    ObjectResponse<AffectedGenomicModel> get(@PathParam("curie") String curie);
    
    @POST
    @Path("/find")
    @JsonView(View.FieldsAndLists.class)
    @Tag(name = "Database Search Endpoints")
    public SearchResponse<AffectedGenomicModel> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
    @POST @Secured
    @Path("/bulk/agmfile")
    public String updateAGMs(AffectedGenomicModelMetaDataFmsDTO agmData);
}
