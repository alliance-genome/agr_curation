package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/condition-relation")
@Tag(name = "CRUD - ConditionRelations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConditionRelationCrudInterface extends BaseCurieCrudInterface<ConditionRelation> {

    @Override
    @POST @Secured
    @Path("/")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<ConditionRelation> create(ConditionRelation entity);
    
}
