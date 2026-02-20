package org.alliancegenome.curation_api.interfaces.crud.associations;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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

	@Operation(summary = "Get sequence targeting reagent gene association by component IDs", description = "Look up a specific sequence targeting reagent gene association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<SequenceTargetingReagentGeneAssociation> getAssociation(@QueryParam("sqtrId") Long alleleId, @QueryParam("relationName") String relationName, @QueryParam("geneId") Long geneId);
}
