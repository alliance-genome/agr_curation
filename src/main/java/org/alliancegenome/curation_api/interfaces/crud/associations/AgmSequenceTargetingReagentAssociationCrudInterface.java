package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmSequenceTargetingReagentAssociationDTO;
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

@Path("/agmstrassociation")
@Tag(name = "CRUD - AGM Sequence Targeting Reagent Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AgmSequenceTargetingReagentAssociationCrudInterface extends BaseIdCrudInterface<AgmSequenceTargetingReagentAssociation> {

	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AgmSequenceTargetingReagentAssociation> getAssociation(@QueryParam("agmId") Long agmId, @QueryParam("relationName") String relationName, @QueryParam("strId") Long strId);

	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateAgmStrAssociations(@PathParam("dataProvider") String dataProvider, List<AgmSequenceTargetingReagentAssociationDTO> associationData);
}
