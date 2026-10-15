package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructCassetteAssociationDTO;
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
@Path("/constructcassetteassociation")
@Tag(name = "CRUD - Construct Cassette Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructCassetteAssociationCrudInterface extends BaseIdCrudInterface<ConstructCassetteAssociation> {

	@Operation(summary = "Bulk load construct cassette association data", description = "Bulk load construct cassette association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateConstructCassetteAssociations(@PathParam("dataProvider") String dataProvider, List<ConstructCassetteAssociationDTO> associationData);

	@Operation(summary = "Get construct cassette association by component IDs", description = "Look up a specific construct cassette association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<ConstructCassetteAssociation> getAssociation(@QueryParam("constructId") Long constructId, @QueryParam("relationName") String relationName, @QueryParam("cassetteId") Long cassetteId);

	@Operation(summary = "Validate construct cassette association", description = "Validate a construct cassette association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<ConstructCassetteAssociation> validate(ConstructCassetteAssociation entity);
}
