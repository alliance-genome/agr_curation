package org.alliancegenome.curation_api.interfaces.crud.associations.constructAssociations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
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

@Path("/constructgenomicentityassociation")
@Tag(name = "CRUD - Construct Genomic Entity Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructGenomicEntityAssociationCrudInterface extends BaseIdCrudInterface<ConstructGenomicEntityAssociation> {

	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateConstructGenomicEntityAssociations(@PathParam("dataProvider") String dataProvider, List<ConstructGenomicEntityAssociationDTO> associationData);
	
	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<ConstructGenomicEntityAssociation> getAssociation(@QueryParam("constructId") Long constructId, @QueryParam("relationName") String relationName, @QueryParam("genomicEntityId") Long genomicEntityId);
	
	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<ConstructGenomicEntityAssociation> validate(ConstructGenomicEntityAssociation entity);
}
