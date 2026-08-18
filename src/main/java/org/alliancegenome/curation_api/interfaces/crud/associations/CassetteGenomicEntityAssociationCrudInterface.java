package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteGenomicEntityAssociationDTO;
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

@Path("/cassettegenomicentityassociation")
@Tag(name = "CRUD - Cassette Genomic Entity Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CassetteGenomicEntityAssociationCrudInterface extends BaseIdCrudInterface<CassetteGenomicEntityAssociation> {

	@Operation(summary = "Bulk load cassette genomic entity association data", description = "Bulk load cassette genomic entity association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateCassetteGenomicEntityAssociations(@PathParam("dataProvider") String dataProvider, List<CassetteGenomicEntityAssociationDTO> associationData);

	@Operation(summary = "Get cassette genomic entity association by component IDs", description = "Look up a specific cassette genomic entity association by its component entity IDs and relation")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteGenomicEntityAssociation> getAssociation(@QueryParam("cassetteId") Long cassetteId, @QueryParam("relationName") String relationName, @QueryParam("genomicEntityId") Long genomicEntityId);

	@Operation(summary = "Validate cassette genomic entity association", description = "Validate a cassette genomic entity association entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<CassetteGenomicEntityAssociation> validate(CassetteGenomicEntityAssociation entity);
}
