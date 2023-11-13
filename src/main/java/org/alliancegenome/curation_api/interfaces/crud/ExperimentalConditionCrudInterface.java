package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/experimental-condition")
@Tag(name = "CRUD - ExperimentalConditions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExperimentalConditionCrudInterface extends BaseIdCrudInterface<ExperimentalCondition> {

	@GET
	@Path("/findBy/{conditionSummary}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<ExperimentalCondition> get(@PathParam("conditionSummary") String conditionSummary);

}
