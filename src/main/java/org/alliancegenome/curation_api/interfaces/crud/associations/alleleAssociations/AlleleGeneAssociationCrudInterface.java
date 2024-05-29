package org.alliancegenome.curation_api.interfaces.crud.associations.alleleAssociations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
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

@Path("/allelegeneassociation")
@Tag(name = "CRUD - Allele Gene Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleGeneAssociationCrudInterface extends BaseIdCrudInterface<AlleleGeneAssociation> {

	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateAlleleGeneAssociations(@PathParam("dataProvider") String dataProvider, List<AlleleGeneAssociationDTO> associationData);
	
	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<AlleleGeneAssociation> getAssociation(@QueryParam("alleleId") Long alleleId, @QueryParam("relationName") String relationName, @QueryParam("geneId") Long geneId);
	
	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation entity);
}
