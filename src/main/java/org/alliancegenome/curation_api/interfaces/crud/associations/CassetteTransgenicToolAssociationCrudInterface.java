package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteTransgenicToolAssociationDTO;
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
@Path("/cassettetransgenictoolassociation")
@Tag(name = "CRUD - Cassette Transgenic Tool Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CassetteTransgenicToolAssociationCrudInterface extends BaseIdCrudInterface<CassetteTransgenicToolAssociation> {

	@Operation(summary = "Bulk load cassette transgenic tool association data", description = "Bulk load cassette transgenic tool association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateCassetteTransgenicToolAssociations(@PathParam("dataProvider") String dataProvider, List<CassetteTransgenicToolAssociationDTO> associationData);

	@Operation(summary = "Get cassette transgenic tool association by component IDs", description = "Look up a specific cassette transgenic tool association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteTransgenicToolAssociation> getAssociation(@QueryParam("cassetteId") Long cassetteId, @QueryParam("relationName") String relationName, @QueryParam("transgenicToolId") Long transgenicToolId);

	@Operation(summary = "Validate cassette transgenic tool association", description = "Validate a cassette transgenic tool association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteTransgenicToolAssociation> validate(CassetteTransgenicToolAssociation entity);
}
