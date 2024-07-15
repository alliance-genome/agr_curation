package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyIngestFmsDTO;
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

@Path("/paralogy")
@Tag(name = "CRUD - Paralogy")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneToGeneParalogyCrudInterface extends BaseIdCrudInterface<GeneToGeneParalogy> {

	@POST
	@Path("/bulk/{dataProvider}/paralogyfile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateParalogy(@PathParam("dataProvider") String dataProvider, ParalogyIngestFmsDTO paralogyData);

}
