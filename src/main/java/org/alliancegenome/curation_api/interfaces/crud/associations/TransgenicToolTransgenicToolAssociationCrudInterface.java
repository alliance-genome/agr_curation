package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.TransgenicToolTransgenicToolAssociationDTO;
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

/** SCRUM-6543. Mirrors AgmAgmAssociationCrudInterface. */
@Path("/transgenictooltransgenictoolassociation")
@Tag(name = "CRUD - Transgenic Tool Transgenic Tool Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TransgenicToolTransgenicToolAssociationCrudInterface extends BaseIdCrudInterface<TransgenicToolTransgenicToolAssociation> {

	@Operation(summary = "Get transgenic tool transgenic tool association by component IDs", description = "Look up a specific transgenic tool transgenic tool association by its two transgenic tool IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<TransgenicToolTransgenicToolAssociation> getAssociation(@QueryParam("transgenicToolSubjectId") Long subjectId, @QueryParam("relationName") String relationName, @QueryParam("transgenicToolObjectId") Long objectId);

	@Operation(summary = "Bulk load transgenic tool transgenic tool association data", description = "Bulk load transgenic tool transgenic tool association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateTransgenicToolTransgenicToolAssociations(@PathParam("dataProvider") String dataProvider, List<TransgenicToolTransgenicToolAssociationDTO> associationData);
}
