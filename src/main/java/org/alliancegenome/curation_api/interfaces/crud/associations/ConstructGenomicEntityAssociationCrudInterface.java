package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructGenomicEntityAssociationDTO;
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

@Path("/constructgenomicentityassociation")
@Tag(name = "CRUD - Construct Genomic Entity Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructGenomicEntityAssociationCrudInterface extends BaseIdCrudInterface<ConstructGenomicEntityAssociation> {

	@Operation(summary = "Bulk load construct genomic entity association data", description = "Bulk load construct genomic entity association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateConstructGenomicEntityAssociations(@PathParam("dataProvider") String dataProvider, List<ConstructGenomicEntityAssociationDTO> associationData);
	
	@Operation(summary = "Get construct genomic entity association by component IDs", description = "Look up a specific construct genomic entity association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<ConstructGenomicEntityAssociation> getAssociation(@QueryParam("constructId") Long constructId, @QueryParam("relationName") String relationName, @QueryParam("genomicEntityId") Long genomicEntityId);
	
	@Operation(summary = "Validate construct genomic entity association", description = "Validate a construct genomic entity association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<ConstructGenomicEntityAssociation> validate(ConstructGenomicEntityAssociation entity);
}
