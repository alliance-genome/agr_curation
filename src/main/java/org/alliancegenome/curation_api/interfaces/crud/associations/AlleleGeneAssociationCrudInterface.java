package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleGeneAssociationDTO;
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

@Path("/allelegeneassociation")
@Tag(name = "CRUD - Allele Gene Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleGeneAssociationCrudInterface extends BaseIdCrudInterface<AlleleGeneAssociation> {

	@Operation(summary = "Bulk load allele gene association data", description = "Bulk load allele gene association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateAlleleGeneAssociations(@PathParam("dataProvider") String dataProvider, List<AlleleGeneAssociationDTO> associationData);
	
	@Operation(summary = "Get allele gene association by component IDs", description = "Look up a specific allele gene association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AlleleGeneAssociation> getAssociation(@QueryParam("alleleId") Long alleleId, @QueryParam("relationName") String relationName, @QueryParam("geneId") Long geneId);
	
	@Operation(summary = "Validate allele gene association", description = "Validate a allele gene association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation entity);
}
