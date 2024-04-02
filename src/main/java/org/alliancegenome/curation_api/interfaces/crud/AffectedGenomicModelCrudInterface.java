package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseReadIdentifierCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.SubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
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

@Path("/agm")
@Tag(name = "CRUD - Affected Genomic Models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelCrudInterface extends SubmittedObjectCrudInterface<AffectedGenomicModel>, 
	BaseReadIdentifierCurieControllerInterface<AffectedGenomicModel>,
	BaseUpsertControllerInterface<AffectedGenomicModel, AffectedGenomicModelDTO> {

	@POST
	@Path("/bulk/{dataProvider}/agms")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateAGMs(@PathParam("dataProvider") String dataProvider, List<AffectedGenomicModelDTO> agmData);
}
