package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
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
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/condition-relation")
@Tag(name = "CRUD - ConditionRelations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConditionRelationCrudInterface extends BaseIdCrudInterface<ConditionRelation> {

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.ConditionRelationCreateView.class)
	ObjectResponse<ConditionRelation> create(ConditionRelation entity);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.ConditionRelationUpdateView.class)
	ObjectResponse<ConditionRelation> update(ConditionRelation entity);

	@Override
	@GET
	@Path("/{id}")
	@JsonView(CurationView.ConditionRelationView.class)
	ObjectResponse<ConditionRelation> getById(@PathParam("id") Long id);

	@Operation(summary = "Validate condition relation", description = "Validate a condition relation entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.ConditionRelationView.class)
	ObjectResponse<ConditionRelation> validate(ConditionRelation entity);

	@Operation(summary = "Find experiments", description = "Search for experimental condition relation records using field-level filters")
	@POST
	@Path("/find-experiments")
	@JsonView(CurationView.FieldsAndLists.class)
	SearchResponse<ConditionRelation> findExperiments(@RequestBody HashMap<String, Object> params);

}
