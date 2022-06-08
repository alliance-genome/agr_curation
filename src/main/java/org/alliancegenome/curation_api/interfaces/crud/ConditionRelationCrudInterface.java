package org.alliancegenome.curation_api.interfaces.crud;


import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/condition-relation")
@Tag(name = "CRUD - ConditionRelations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConditionRelationCrudInterface extends BaseIdCrudInterface<ConditionRelation> {

    @Override
    @POST
    @Path("/")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<ConditionRelation> create(ConditionRelation entity);

    @PUT
    @Path("/")
    @JsonView(View.ConditionRelationUpdateView.class)
    public ObjectResponse<ConditionRelation> update(ConditionRelation entity);

    @POST
    @Path("/validate")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<ConditionRelation> validate(ConditionRelation entity);


}
