package org.alliancegenome.curation_api.interfaces.base;

import java.util.Set;

import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseDeleteCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseUpdateControllerInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Tag(name = "CRUD - Ontology - Bulk")
public interface BaseOntologyTermCrudInterface<E extends OntologyTerm> extends
	BaseCreateControllerInterface<E>,
	BaseReadCurieControllerInterface<E>,
	BaseUpdateControllerInterface<E>,
	BaseDeleteCurieControllerInterface<E>,
	BaseSearchControllerInterface<E>,
	BaseFindControllerInterface<E>,
	BaseReindexControllerInterface {

	void init();

	@POST
	@Path("/bulk/owl")
	@Consumes(MediaType.APPLICATION_XML)
	@Operation(summary = "Bulk load ontology terms from OWL", description = "Load ontology terms from an OWL/XML document. Can run synchronously or asynchronously.")
	@RequestBody(description = "OWL/XML ontology document")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "Status message indicating load result")
	)
	String updateTerms(@Parameter(description = "Run asynchronously") @DefaultValue("true") @QueryParam("async") boolean async, @RequestBody String fullText);

	@GET
	@Path("/rootNodes")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Get root nodes", description = "Retrieve all root nodes (terms with no parents) in this ontology")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "List of root ontology terms")
	)
	ObjectListResponse<E> getRootNodes();

	@GET
	@Path("/{curie}/descendants")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Get descendants of term", description = "Retrieve all descendant terms (children, grandchildren, etc.) of the given term")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "List of descendant ontology terms")
	)
	ObjectListResponse<E> getDescendants(
		@Parameter(description = "Curie of the parent term") @PathParam("curie") String curie,
		@Parameter(description = "Relation types to traverse (e.g. is_a, part_of)") @QueryParam("relationTypes") Set<String> relationTypes);

	@GET
	@Path("/{curie}/children")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Get children of term", description = "Retrieve the direct child terms of the given term")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "List of child ontology terms")
	)
	ObjectListResponse<E> getChildren(
		@Parameter(description = "Curie of the parent term") @PathParam("curie") String curie,
		@Parameter(description = "Relation types to traverse (e.g. is_a, part_of)") @QueryParam("relationTypes") Set<String> relationTypes);

	@GET
	@Path("/{curie}/parents")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Get parents of term", description = "Retrieve the direct parent terms of the given term")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "List of parent ontology terms")
	)
	ObjectListResponse<E> getParents(
		@Parameter(description = "Curie of the child term") @PathParam("curie") String curie,
		@Parameter(description = "Relation types to traverse (e.g. is_a, part_of)") @QueryParam("relationTypes") Set<String> relationTypes);

	@GET
	@Path("/{curie}/ancestors")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Get ancestors of term", description = "Retrieve all ancestor terms (parents, grandparents, etc.) of the given term")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "List of ancestor ontology terms")
	)
	ObjectListResponse<E> getAncestors(
		@Parameter(description = "Curie of the descendant term") @PathParam("curie") String curie,
		@Parameter(description = "Relation types to traverse (e.g. is_a, part_of)") @QueryParam("relationTypes") Set<String> relationTypes);
}
