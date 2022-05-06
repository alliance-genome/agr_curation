package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.fms.dto.GeneMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/gene")
@Tag(name = "CRUD - Genes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneCrudInterface extends BaseCurieCrudInterface<Gene> {

    @Override
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<Gene> get(@PathParam("curie") String curie);

    @POST
    @Path("/bulk/bgifile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateGenes(GeneMetaDataFmsDTO geneData);
    
}
