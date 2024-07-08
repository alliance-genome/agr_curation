package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseDeleteCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseUpdateControllerInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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
	String updateTerms(@DefaultValue("true") @QueryParam("async") boolean async, @RequestBody String fullText);

	@GET
	@Path("/rootNodes")
	@JsonView(View.FieldsOnly.class)
	ObjectListResponse<E> getRootNodes();

	@GET
	@Path("/{curie}/descendants")
	@JsonView(View.FieldsOnly.class)
	ObjectListResponse<E> getDescendants(@PathParam("curie") String curie);

	@GET
	@Path("/{curie}/children")
	@JsonView(View.FieldsOnly.class)
	ObjectListResponse<E> getChildren(@PathParam("curie") String curie);

	@GET
	@Path("/{curie}/parents")
	@JsonView(View.FieldsOnly.class)
	ObjectListResponse<E> getParents(@PathParam("curie") String curie);

	@GET
	@Path("/{curie}/ancestors")
	@JsonView(View.FieldsOnly.class)
	ObjectListResponse<E> getAncestors(@PathParam("curie") String curie);
}
