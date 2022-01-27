package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/experimental-condition")
@Tag(name = "CRUD - ExperimentalConditions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExperimentalConditionCrudInterface extends BaseCurieCrudInterface<ExperimentalCondition> {

    @POST
    @Path("/find")
    @Tag(name = "Database Search Endpoints")
    @JsonView(View.FieldsAndLists.class)
    public SearchResponse<ExperimentalCondition> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);


}
