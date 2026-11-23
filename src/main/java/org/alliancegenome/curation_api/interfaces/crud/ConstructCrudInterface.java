package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
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
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/construct")
@Tag(name = "CRUD - Constructs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructCrudInterface extends BaseSubmittedObjectCrudInterface<Construct>, BaseUpsertControllerInterface<Construct, ConstructDTO> {

	@Operation(summary = "Get synonyms for construct", description = "Retrieve all synonyms belonging to this construct")
	@GET
	@Path("/{id}/synonyms")
	@JsonView(CurationView.ConstructDetailView.class)
	ObjectListResponse<ConstructSynonymSlotAnnotation> getSynonyms(@PathParam("id") Long id);

	@Operation(summary = "Replace synonyms for construct", description = "Replace this construct's synonyms with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/synonyms")
	@JsonView(CurationView.ConstructDetailView.class)
	ObjectListResponse<ConstructSynonymSlotAnnotation> updateSynonyms(@PathParam("id") Long id, List<ConstructSynonymSlotAnnotation> synonyms);

	@Operation(summary = "Get secondary IDs for construct", description = "Retrieve all secondary IDs belonging to this construct")
	@GET
	@Path("/{id}/secondaryids")
	ObjectListResponse<String> getSecondaryIds(@PathParam("id") Long id);

	@Operation(summary = "Replace secondary IDs for construct", description = "Replace this construct's secondary IDs with the submitted list, removing any that are omitted")
	@PUT
	@Path("/{id}/secondaryids")
	ObjectListResponse<String> updateSecondaryIds(@PathParam("id") Long id, List<String> secondaryIds);

	@Operation(summary = "Get related notes for construct", description = "Retrieve all related notes belonging to this construct")
	@GET
	@Path("/{id}/relatednotes")
	@JsonView(CurationView.ConstructDetailView.class)
	ObjectListResponse<Note> getRelatedNotes(@PathParam("id") Long id);

	@Operation(summary = "Replace related notes for construct", description = "Replace this construct's related notes with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/relatednotes")
	@JsonView(CurationView.ConstructDetailView.class)
	ObjectListResponse<Note> updateRelatedNotes(@PathParam("id") Long id, List<Note> relatedNotes);

	@Operation(summary = "Get references for construct", description = "Retrieve all references attached to this construct")
	@GET
	@Path("/{id}/references")
	@JsonView(CurationView.ReferenceView.class)
	ObjectListResponse<Reference> getReferences(@PathParam("id") Long id);

	@Operation(summary = "Replace references for construct", description = "Replace this construct's references with the submitted list, detaching any that are omitted")
	@PUT
	@Path("/{id}/references")
	@JsonView(CurationView.ReferenceView.class)
	ObjectListResponse<Reference> updateReferences(@PathParam("id") Long id, List<Reference> references);

	@Operation(summary = "Get cassette associations for construct", description = "Retrieve all cassette associations belonging to this construct")
	@GET
	@Path("/{id}/cassetteassociations")
	@JsonView(CurationView.ConstructView.class)
	ObjectListResponse<ConstructCassetteAssociation> getCassetteAssociations(@PathParam("id") Long id);

	@Operation(summary = "Replace cassette associations for construct", description = "Replace this construct's cassette associations with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/cassetteassociations")
	@JsonView(CurationView.ConstructView.class)
	ObjectListResponse<ConstructCassetteAssociation> updateCassetteAssociations(@PathParam("id") Long id, List<ConstructCassetteAssociation> cassetteAssociations);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.ConstructView.class)
	ObjectResponse<Construct> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.ConstructView.class)
	ObjectResponse<Construct> update(Construct entity);

	@Operation(summary = "Update construct from its detail page", description = "Update a construct's own fields, leaving its lists and associations as they are stored")
	@PUT
	@Path("/updateDetail")
	@JsonView(CurationView.ConstructDetailView.class)
	ObjectResponse<Construct> updateDetail(Construct entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.ConstructView.class)
	ObjectResponse<Construct> create(Construct entity);

	@Operation(summary = "Bulk load construct data", description = "Bulk load construct records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/constructs")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateConstructs(@PathParam("dataProvider") String dataProvider, List<ConstructDTO> constructData);

}
