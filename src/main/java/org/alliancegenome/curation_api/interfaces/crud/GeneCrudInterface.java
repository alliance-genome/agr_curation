package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/gene")
@Tag(name = "CRUD - Genes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneCrudInterface extends BaseSubmittedObjectCrudInterface<Gene>, BaseUpsertControllerInterface<Gene, GeneDTO> {

	@Operation(summary = "Get cross references for gene", description = "Retrieve all cross references belonging to this gene")
	@GET
	@Path("/{id}/crossreferences")
	@JsonView(CurationView.CrossReferenceView.class)
	ObjectListResponse<CrossReference> getCrossReferences(@PathParam("id") Long id);

	@Operation(summary = "Replace cross references for gene", description = "Replace this gene's cross references with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/crossreferences")
	@JsonView(CurationView.CrossReferenceView.class)
	ObjectListResponse<CrossReference> updateCrossReferences(@PathParam("id") Long id, List<CrossReference> crossReferences);

	@Operation(summary = "Get synonyms for gene", description = "Retrieve all synonyms belonging to this gene")
	@GET
	@Path("/{id}/synonyms")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectListResponse<GeneSynonymSlotAnnotation> getSynonyms(@PathParam("id") Long id);

	@Operation(summary = "Replace synonyms for gene", description = "Replace this gene's synonyms with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/synonyms")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectListResponse<GeneSynonymSlotAnnotation> updateSynonyms(@PathParam("id") Long id, List<GeneSynonymSlotAnnotation> synonyms);

	@Operation(summary = "Get secondary IDs for gene", description = "Retrieve all secondary IDs belonging to this gene")
	@GET
	@Path("/{id}/secondaryids")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectListResponse<GeneSecondaryIdSlotAnnotation> getSecondaryIds(@PathParam("id") Long id);

	@Operation(summary = "Replace secondary IDs for gene", description = "Replace this gene's secondary IDs with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/secondaryids")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectListResponse<GeneSecondaryIdSlotAnnotation> updateSecondaryIds(@PathParam("id") Long id, List<GeneSecondaryIdSlotAnnotation> secondaryIds);

	@Operation(summary = "Get related notes for gene", description = "Retrieve all related notes belonging to this gene")
	@GET
	@Path("/{id}/relatednotes")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectListResponse<Note> getRelatedNotes(@PathParam("id") Long id);

	@Operation(summary = "Replace related notes for gene", description = "Replace this gene's related notes with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/relatednotes")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectListResponse<Note> updateRelatedNotes(@PathParam("id") Long id, List<Note> relatedNotes);

	@Operation(summary = "Bulk load gene data", description = "Bulk load gene records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/genes")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateGenes(@PathParam("dataProvider") String dataProvider, List<GeneDTO> geneData);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectResponse<Gene> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.GeneView.class)
	ObjectResponse<Gene> update(Gene entity);

	@Operation(summary = "Update gene with full detail", description = "Update a gene entity and return the detailed view")
	@PUT
	@Path("/updateDetail")
	@JsonView(CurationView.GeneDetailView.class)
	ObjectResponse<Gene> updateDetail(Gene entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.GeneView.class)
	ObjectResponse<Gene> create(Gene entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.GeneView.class)
	SearchResponse<Gene> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.GeneView.class })
	SearchResponse<Gene> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

}
