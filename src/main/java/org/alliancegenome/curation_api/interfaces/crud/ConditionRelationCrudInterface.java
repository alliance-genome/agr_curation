package org.alliancegenome.curation_api.interfaces.crud;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
	ObjectResponse<ConditionRelation> create(ConditionRelation entity);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<ConditionRelation> update(ConditionRelation entity);

	@Override
	@GET
	@Path("/{id}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<ConditionRelation> get(@PathParam("id") Long id);

	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<ConditionRelation> validate(ConditionRelation entity);

	@POST
	@Path("/find-experiments")
	@JsonView(View.FieldsAndLists.class)
	SearchResponse<ConditionRelation> findExperiments(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
