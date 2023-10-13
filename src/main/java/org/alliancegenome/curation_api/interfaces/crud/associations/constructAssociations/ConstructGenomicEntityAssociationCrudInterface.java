package org.alliancegenome.curation_api.interfaces.crud.associations.constructAssociations;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/constructgenomicentityassociation")
@Tag(name = "CRUD - Construct Genomic Entity Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructGenomicEntityAssociationCrudInterface extends BaseIdCrudInterface<ConstructGenomicEntityAssociation> {

	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateConstructGenomicEntityAssociations(@PathParam("dataProvider") String dataProvider, List<ConstructGenomicEntityAssociationDTO> associationData);
	
	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<ConstructGenomicEntityAssociation> getAssociation(@QueryParam("constructId") Long constructId, @QueryParam("relationName") String relationName, @QueryParam("genomicEntityCurie") String geneCurie);
	
	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<ConstructGenomicEntityAssociation> validate(ConstructGenomicEntityAssociation entity);
}
