package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/experimental-condition")
@Tag(name = "CRUD - ExperimentalConditions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExperimentalConditionCrudInterface extends BaseIdCrudInterface<ExperimentalCondition> {

	@Operation(summary = "Get experimental condition by identifier", description = "Retrieve a single experimental condition by its identifier")
	@GET
	@Path("/findBy/{conditionSummary}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<ExperimentalCondition> get(@PathParam("conditionSummary") String conditionSummary);

}
