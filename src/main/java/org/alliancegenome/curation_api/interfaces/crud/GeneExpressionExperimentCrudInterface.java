package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
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


@Path("/gene-expression-experiment")
@Tag(name = "CRUD - Gene Expression Experiments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionExperimentCrudInterface extends BaseIdCrudInterface<GeneExpressionExperiment> {
	@Operation(summary = "Get gene expression experiment by identifier", description = "Retrieve a single gene expression experiment by its identifier")
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<GeneExpressionExperiment> getByIdentifier(@PathParam("identifier") String identifier);
}
