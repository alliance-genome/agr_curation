package org.alliancegenome.curation_api.interfaces.crud.orthology;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/orthologygenerated")
@Tag(name = "CRUD - Orthology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneToGeneOrthologyGeneratedCrudInterface extends BaseIdCrudInterface<GeneToGeneOrthologyGenerated> {

	@POST
	@Path("/bulk/{dataProvider}/orthologyfile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateOrthology(@PathParam("dataProvider") String dataProvider, OrthologyIngestFmsDTO orthologyData);
}