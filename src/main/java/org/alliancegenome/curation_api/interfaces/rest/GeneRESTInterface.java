package org.alliancegenome.curation_api.interfaces.rest;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gene")
@Tag(name = "Genes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneRESTInterface extends BaseCrudRESTInterface<Gene> {

    @JsonView(View.FieldsAndLists.class)
    @Override
    Gene get(@PathParam("id") String id);

}
