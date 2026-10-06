package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteStrAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/** SCRUM-6535. Mirrors ConstructGenomicEntityAssociationCrudInterface. */
@Path("/cassettestrassociation")
@Tag(name = "CRUD - Cassette STR Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CassetteStrAssociationCrudInterface extends BaseIdCrudInterface<CassetteStrAssociation> {

	@Operation(summary = "Bulk load cassette STR association data", description = "Bulk load cassette STR association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateCassetteStrAssociations(@PathParam("dataProvider") String dataProvider, List<CassetteStrAssociationDTO> associationData);

	@Operation(summary = "Get cassette STR association by component IDs", description = "Look up a specific cassette STR association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteStrAssociation> getAssociation(@QueryParam("cassetteId") Long cassetteId, @QueryParam("relationName") String relationName, @QueryParam("sequenceTargetingReagentId") Long sequenceTargetingReagentId);

	@Operation(summary = "Validate cassette STR association", description = "Validate a cassette STR association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteStrAssociation> validate(CassetteStrAssociation entity);
}
