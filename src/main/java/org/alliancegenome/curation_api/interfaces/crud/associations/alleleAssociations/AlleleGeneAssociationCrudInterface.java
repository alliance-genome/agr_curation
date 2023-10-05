package org.alliancegenome.curation_api.interfaces.crud.associations.alleleAssociations;

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
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allelegeneassociation")
@Tag(name = "CRUD - Allele Gene Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleGeneAssociationCrudInterface extends BaseIdCrudInterface<AlleleGeneAssociation> {

	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateGeneAlleleAssociations(@PathParam("dataProvider") String dataProvider, List<AlleleGeneAssociationDTO> associationData);
	
	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AlleleGeneAssociation> getAssociation(@QueryParam("allele_curie") String alleleCurie, @QueryParam("relationName") String relationName, @QueryParam("geneCurie") String geneCurie);
	
	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation entity);
}
