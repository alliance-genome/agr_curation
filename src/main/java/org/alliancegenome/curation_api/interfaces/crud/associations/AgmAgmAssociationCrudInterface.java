package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAgmAssociationDTO;
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

@Path("/agmagmassociation")
@Tag(name = "CRUD - AGM AGM Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AgmAgmAssociationCrudInterface extends BaseIdCrudInterface<AgmAgmAssociation> {

	@Operation(summary = "Get agm agm association by component IDs", description = "Look up a specific agm agm association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AgmAgmAssociation> getAssociation(@QueryParam("agmSubjectId") Long agmId, @QueryParam("relationName") String relationName, @QueryParam("agmObjectId") Long strId);

	@Operation(summary = "Bulk load agm agm association data", description = "Bulk load agm agm association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateAgmAgmAssociations(@PathParam("dataProvider") String dataProvider, List<AgmAgmAssociationDTO> associationData);
}
