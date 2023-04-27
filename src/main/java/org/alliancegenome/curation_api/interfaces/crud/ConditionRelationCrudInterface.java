package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
	SearchResponse<ConditionRelation> findExperiments(@RequestBody HashMap<String, Object> params);

}
