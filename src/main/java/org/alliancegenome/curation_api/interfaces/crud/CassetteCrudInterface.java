package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** SCRUM-6535. Mirrors ConstructCrudInterface. */
@Path("/cassette")
@Tag(name = "CRUD - Cassettes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CassetteCrudInterface extends BaseSubmittedObjectCrudInterface<Cassette>, BaseUpsertControllerInterface<Cassette, CassetteDTO> {

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.CassetteView.class)
	ObjectResponse<Cassette> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.CassetteView.class)
	ObjectResponse<Cassette> update(Cassette entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.CassetteView.class)
	ObjectResponse<Cassette> create(Cassette entity);

	@Operation(summary = "Get free text components for cassette", description = "The cassette's free text components")
	@GET
	@Path("/{id}/components")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteComponentSlotAnnotation> getComponents(@PathParam("id") Long id);

	@Operation(summary = "Replace free text components for cassette", description = "Replace the cassette's free text components with the submitted list: an entry with an id updates that row, one without creates one, and rows the list omits are deleted")
	@PUT
	@Path("/{id}/components")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteComponentSlotAnnotation> updateComponents(@PathParam("id") Long id, List<CassetteComponentSlotAnnotation> rows);

	@Operation(summary = "Get genomic entity associations for cassette", description = "The cassette's genomic entity associations")
	@GET
	@Path("/{id}/genomic-entity-associations")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteGenomicEntityAssociation> getGenomicEntityAssociations(@PathParam("id") Long id);

	@Operation(summary = "Replace genomic entity associations for cassette", description = "Replace the cassette's genomic entity associations with the submitted list: an entry with an id updates that row, one without creates one, and rows the list omits are deleted")
	@PUT
	@Path("/{id}/genomic-entity-associations")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteGenomicEntityAssociation> updateGenomicEntityAssociations(@PathParam("id") Long id, List<CassetteGenomicEntityAssociation> rows);

	@Operation(summary = "Get transgenic tool associations for cassette", description = "The cassette's transgenic tool associations")
	@GET
	@Path("/{id}/transgenic-tool-associations")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteTransgenicToolAssociation> getTransgenicToolAssociations(@PathParam("id") Long id);

	@Operation(summary = "Replace transgenic tool associations for cassette", description = "Replace the cassette's transgenic tool associations with the submitted list: an entry with an id updates that row, one without creates one, and rows the list omits are deleted")
	@PUT
	@Path("/{id}/transgenic-tool-associations")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteTransgenicToolAssociation> updateTransgenicToolAssociations(@PathParam("id") Long id, List<CassetteTransgenicToolAssociation> rows);

	@Operation(summary = "Get sequence targeting reagent associations for cassette", description = "The cassette's sequence targeting reagent associations")
	@GET
	@Path("/{id}/str-associations")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteStrAssociation> getStrAssociations(@PathParam("id") Long id);

	@Operation(summary = "Replace sequence targeting reagent associations for cassette", description = "Replace the cassette's sequence targeting reagent associations with the submitted list: an entry with an id updates that row, one without creates one, and rows the list omits are deleted")
	@PUT
	@Path("/{id}/str-associations")
	@JsonView(CurationView.CassetteView.class)
	ObjectListResponse<CassetteStrAssociation> updateStrAssociations(@PathParam("id") Long id, List<CassetteStrAssociation> rows);

	@Operation(summary = "Bulk load cassette data", description = "Bulk load cassette records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/cassettes")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateCassettes(@PathParam("dataProvider") String dataProvider, List<CassetteDTO> data);

}
