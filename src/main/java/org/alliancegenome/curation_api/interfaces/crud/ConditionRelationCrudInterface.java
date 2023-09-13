package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

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
	@JsonView(View.ConditionRelationUpdateView.class)
	ObjectResponse<ConditionRelation> update(ConditionRelation entity);

	@Override
	@GET
	@Path("/{id}")
	@JsonView(View.ConditionRelationView.class)
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
