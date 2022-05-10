package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.HashMap;

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

    @POST
    @Path("/validate")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<ConditionRelation> validate(ConditionRelation entity);

	@POST
	@Path("/handles/search")
	@Tag(name = "Database Search Endpoints")
	@JsonView(View.FieldsAndLists.class)
	public SearchResponse<ConditionRelation> findHandles(
		@DefaultValue("0") @QueryParam("page") Integer page,
		@DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody HashMap<String, Object> params);

}
