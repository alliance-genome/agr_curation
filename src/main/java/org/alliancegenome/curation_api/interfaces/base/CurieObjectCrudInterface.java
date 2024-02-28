package org.alliancegenome.curation_api.interfaces.base;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.constraints.Null;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CurieObjectCrudInterface<E extends CurieObject> {

	@POST
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> create(E entity);

	@POST
	@Path("/multiple")
	@JsonView(View.FieldsOnly.class)
	public ObjectListResponse<E> create(List<E> entities);

	@GET
	@Path("/{curie}")
	@JsonView(View.FieldsOnly.class)
	@APIResponses(
			@APIResponse(description = "Get the Entity by CURIE",
			responseCode = "200",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Null.class)))
			)
	public ObjectResponse<E> get(@PathParam("curie") String curie);

	@PUT
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> update(E entity);

	@DELETE
	@Path("/{curie}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> delete(@PathParam("curie") String curie);

	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.FieldsAndLists.class)
	public SearchResponse<E> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.FieldsAndLists.class })
	public SearchResponse<E> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@GET
	@Path("/reindex")
	@Tag(name = "Reindex Endpoints")
	public void reindex(
		@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("4") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);

}
