package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AlleleMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele")
@Tag(name = "CRUD - Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleCrudInterface extends BaseCurieCrudInterface<Allele> {

    @POST @Secured
    @Path("/bulk/{taxonID}/allelefile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateAlleles(@PathParam("taxonID") String taxonID, AlleleMetaDataFmsDTO alleleData);

    @Override
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<Allele> get(@PathParam("curie") String curie);

}
