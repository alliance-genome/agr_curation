package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
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
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/cassetteassociation")
@Tag(name = "CRUD - Cassette Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CassetteAssociationCrudInterface extends BaseIdCrudInterface<CassetteAssociation> {

	@Operation(summary = "Bulk load cassette association data", description = "Bulk load cassette association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateCassetteAssociations(@PathParam("dataProvider") String dataProvider, List<CassetteAssociationDTO> associationData);

	@Operation(summary = "Get cassette association by component IDs", description = "Look up a specific cassette association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteAssociation> getAssociation(@QueryParam("cassetteId") Long cassetteId, @QueryParam("relationName") String relationName, @QueryParam("genomicEntityId") Long genomicEntityId);

	@Operation(summary = "Validate cassette association", description = "Validate a cassette association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteAssociation> validate(CassetteAssociation entity);
}
