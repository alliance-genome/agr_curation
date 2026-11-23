package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
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

@Path("/allele")
@Tag(name = "CRUD - Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleCrudInterface extends BaseSubmittedObjectCrudInterface<Allele>, BaseUpsertControllerInterface<Allele, AlleleDTO> {

	@Operation(summary = "Get cross references for allele", description = "Retrieve all cross references belonging to this allele")
	@GET
	@Path("/{id}/crossreferences")
	@JsonView(CurationView.CrossReferenceView.class)
	ObjectListResponse<CrossReference> getCrossReferences(@PathParam("id") Long id);

	@Operation(summary = "Replace cross references for allele", description = "Replace this allele's cross references with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/crossreferences")
	@JsonView(CurationView.CrossReferenceView.class)
	ObjectListResponse<CrossReference> updateCrossReferences(@PathParam("id") Long id, List<CrossReference> crossReferences);

	@Operation(summary = "Get references for allele", description = "Retrieve all references attached to this allele")
	@GET
	@Path("/{id}/references")
	@JsonView(CurationView.ReferenceView.class)
	ObjectListResponse<Reference> getReferences(@PathParam("id") Long id);

	@Operation(summary = "Replace references for allele", description = "Replace this allele's references with the submitted list, detaching any that are omitted")
	@PUT
	@Path("/{id}/references")
	@JsonView(CurationView.ReferenceView.class)
	ObjectListResponse<Reference> updateReferences(@PathParam("id") Long id, List<Reference> references);

	@Operation(summary = "Get gene associations for allele", description = "Retrieve all gene associations belonging to this allele")
	@GET
	@Path("/{id}/geneassociations")
	@JsonView(CurationView.AlleleGeneAssociationView.class)
	ObjectListResponse<AlleleGeneAssociation> getGeneAssociations(@PathParam("id") Long id);

	@Operation(summary = "Replace gene associations for allele", description = "Replace this allele's gene associations with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/geneassociations")
	@JsonView(CurationView.AlleleGeneAssociationView.class)
	ObjectListResponse<AlleleGeneAssociation> updateGeneAssociations(@PathParam("id") Long id, List<AlleleGeneAssociation> geneAssociations);

	@Operation(summary = "Get synonyms for allele", description = "Retrieve all synonyms belonging to this allele")
	@GET
	@Path("/{id}/synonyms")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleSynonymSlotAnnotation> getSynonyms(@PathParam("id") Long id);

	@Operation(summary = "Replace synonyms for allele", description = "Replace this allele's synonyms with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/synonyms")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleSynonymSlotAnnotation> updateSynonyms(@PathParam("id") Long id, List<AlleleSynonymSlotAnnotation> synonyms);

	@Operation(summary = "Get secondary IDs for allele", description = "Retrieve all secondary IDs belonging to this allele")
	@GET
	@Path("/{id}/secondaryids")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleSecondaryIdSlotAnnotation> getSecondaryIds(@PathParam("id") Long id);

	@Operation(summary = "Replace secondary IDs for allele", description = "Replace this allele's secondary IDs with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/secondaryids")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleSecondaryIdSlotAnnotation> updateSecondaryIds(@PathParam("id") Long id, List<AlleleSecondaryIdSlotAnnotation> secondaryIds);

	@Operation(summary = "Get nomenclature events for allele", description = "Retrieve all nomenclature events belonging to this allele")
	@GET
	@Path("/{id}/nomenclatureevents")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleNomenclatureEventSlotAnnotation> getNomenclatureEvents(@PathParam("id") Long id);

	@Operation(summary = "Replace nomenclature events for allele", description = "Replace this allele's nomenclature events with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/nomenclatureevents")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleNomenclatureEventSlotAnnotation> updateNomenclatureEvents(@PathParam("id") Long id, List<AlleleNomenclatureEventSlotAnnotation> nomenclatureEvents);

	@Operation(summary = "Get mutation types for allele", description = "Retrieve all mutation types belonging to this allele")
	@GET
	@Path("/{id}/mutationtypes")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleMutationTypeSlotAnnotation> getMutationTypes(@PathParam("id") Long id);

	@Operation(summary = "Replace mutation types for allele", description = "Replace this allele's mutation types with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/mutationtypes")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleMutationTypeSlotAnnotation> updateMutationTypes(@PathParam("id") Long id, List<AlleleMutationTypeSlotAnnotation> mutationTypes);

	@Operation(summary = "Get functional impacts for allele", description = "Retrieve all functional impacts belonging to this allele")
	@GET
	@Path("/{id}/functionalimpacts")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleFunctionalImpactSlotAnnotation> getFunctionalImpacts(@PathParam("id") Long id);

	@Operation(summary = "Replace functional impacts for allele", description = "Replace this allele's functional impacts with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/functionalimpacts")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleFunctionalImpactSlotAnnotation> updateFunctionalImpacts(@PathParam("id") Long id, List<AlleleFunctionalImpactSlotAnnotation> functionalImpacts);

	@Operation(summary = "Get inheritance modes for allele", description = "Retrieve all inheritance modes belonging to this allele")
	@GET
	@Path("/{id}/inheritancemodes")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleInheritanceModeSlotAnnotation> getInheritanceModes(@PathParam("id") Long id);

	@Operation(summary = "Replace inheritance modes for allele", description = "Replace this allele's inheritance modes with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/inheritancemodes")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<AlleleInheritanceModeSlotAnnotation> updateInheritanceModes(@PathParam("id") Long id, List<AlleleInheritanceModeSlotAnnotation> inheritanceModes);

	@Operation(summary = "Get related notes for allele", description = "Retrieve all related notes belonging to this allele")
	@GET
	@Path("/{id}/relatednotes")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<Note> getRelatedNotes(@PathParam("id") Long id);

	@Operation(summary = "Replace related notes for allele", description = "Replace this allele's related notes with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/relatednotes")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectListResponse<Note> updateRelatedNotes(@PathParam("id") Long id, List<Note> relatedNotes);

	@Operation(summary = "Bulk load allele data", description = "Bulk load allele records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/alleles")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateAlleles(@PathParam("dataProvider") String dataProvider, List<AlleleDTO> alleleData);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectResponse<Allele> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.AlleleView.class)
	ObjectResponse<Allele> update(Allele entity);
	
	@Operation(summary = "Update allele with full detail", description = "Update a allele entity and return the detailed view")
	@PUT
	@Path("/updateDetail")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectResponse<Allele> updateDetail(Allele entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.AlleleDetailView.class)
	ObjectResponse<Allele> create(Allele entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.AlleleView.class)
	SearchResponse<Allele> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.AlleleView.class })
	SearchResponse<Allele> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

}
