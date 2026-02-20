package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleConstructAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
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

@Path("/alleleconstructassociation")
@Tag(name = "CRUD - Allele Construct Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleConstructAssociationCrudInterface extends BaseIdCrudInterface<AlleleConstructAssociation> {

	@Operation(summary = "Bulk load allele construct association data", description = "Bulk load allele construct association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateAlleleConstructAssociations(@PathParam("dataProvider") String dataProvider, List<AlleleConstructAssociationDTO> associationData);

	@Operation(summary = "Get allele construct association by component IDs", description = "Look up a specific allele construct association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AlleleConstructAssociation> getAssociation(@QueryParam("alleleId") Long alleleId, @QueryParam("relationName") String relationName, @QueryParam("constructId") Long constructId);

	@Operation(summary = "Validate allele construct association", description = "Validate a allele construct association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AlleleConstructAssociation> validate(AlleleConstructAssociation entity);

	@Operation(summary = "Find allele construct association for public API", description = "Query allele construct association records with public-facing view")
	@POST
	@Path("/findForPublic")
	@JsonView(CurationView.ForPublic.class)
	SearchResponse<AlleleConstructAssociation> findForPublic(Integer page, Integer limit, HashMap<String, Object> params);
}
