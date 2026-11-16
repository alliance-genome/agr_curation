package org.alliancegenome.curation_api.interfaces.crud.associations;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.HashMap;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/sqtrgeneassociation")
@Tag(name = "CRUD - SQTR Gene Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SequenceTargetingReagentGeneAssociationCrudInterface extends BaseIdCrudInterface<SequenceTargetingReagentGeneAssociation> {

	// A page of associations by field values, e.g. the targeted genes of one STR for the STR table's
	// pop-up ({"sequenceTargetingReagentAssociationSubject.id": <id>}). Implemented by
	// BaseEntityCrudController.find.
	@Operation(summary = "Find sequence targeting reagent gene associations", description = "A page of associations matching exact field values, with dot notation for nested properties, e.g. {\"sequenceTargetingReagentAssociationSubject.id\": 123} for the targeted genes of one STR")
	@POST
	@Path("/find")
	@JsonView(CurationView.SequenceTargetingReagentGeneAssociationView.class)
	SearchResponse<SequenceTargetingReagentGeneAssociation> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Operation(summary = "Get sequence targeting reagent gene association by component IDs", description = "Look up a specific sequence targeting reagent gene association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.SequenceTargetingReagentGeneAssociationView.class)
	ObjectResponse<SequenceTargetingReagentGeneAssociation> getAssociation(@QueryParam("sqtrId") Long alleleId, @QueryParam("relationName") String relationName, @QueryParam("geneId") Long geneId);
}
