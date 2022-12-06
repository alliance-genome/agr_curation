package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

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
