package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyIngestFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/sqtr")
@Tag(name = "CRUD - SQTR")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SequenceTargetingReagentCrudInterface extends BaseIdCrudInterface<SequenceTargetingReagent> {

    @POST 
	@Path("/bulk/{dataProvider}/sqtrfile") 
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateSequenceTargetingReagent(@PathParam("dataProvider") String dataProvider, SequenceTargetingReagentIngestFmsDTO sqtrData);
}
