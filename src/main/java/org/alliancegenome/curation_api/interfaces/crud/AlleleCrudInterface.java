package org.alliancegenome.curation_api.interfaces.crud;


import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AlleleMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/allele")
@Tag(name = "CRUD - Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleCrudInterface extends BaseCurieCrudInterface<Allele> {

    @POST
    @Path("/bulk/allelefile")
    public String updateAlleles(AlleleMetaDataFmsDTO alleleData);

    @POST
    @Path("/find")
    @Tag(name = "Database Search Endpoints")
    @JsonView(View.FieldsAndLists.class)
    public SearchResponse<Allele> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);


    @JsonView(View.FieldsAndLists.class)
    @Override
    ObjectResponse<Allele> get(@PathParam("curie") String curie);

}
