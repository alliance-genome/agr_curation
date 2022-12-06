package org.alliancegenome.curation_api.interfaces.base;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Tag(name = "CRUD - Ontology - Bulk")
public interface BaseOntologyTermCrudInterface<E extends OntologyTerm> extends BaseCurieCrudInterface<E> {

	@POST
	@Path("/bulk/owl")
	@Consumes(MediaType.APPLICATION_XML)
	public String updateTerms(@DefaultValue("true") @QueryParam("async") boolean async, @RequestBody String fullText);

	public void init();

	@GET
	@Path("/rootNodes")
	@JsonView(View.FieldsOnly.class)
	public ObjectListResponse<E> getRootNodes();

	@GET
	@Path("/{curie}/descendants")
	@JsonView(View.FieldsOnly.class)
	public ObjectListResponse<E> getDescendants(@PathParam("curie") String curie);

	@GET
	@Path("/{curie}/children")
	@JsonView(View.FieldsOnly.class)
	public ObjectListResponse<E> getChildren(@PathParam("curie") String curie);

	@GET
	@Path("/{curie}/parents")
	@JsonView(View.FieldsOnly.class)
	public ObjectListResponse<E> getParents(@PathParam("curie") String curie);

	@GET
	@Path("/{curie}/ancestors")
	@JsonView(View.FieldsOnly.class)
	public ObjectListResponse<E> getAncestors(@PathParam("curie") String curie);
}
