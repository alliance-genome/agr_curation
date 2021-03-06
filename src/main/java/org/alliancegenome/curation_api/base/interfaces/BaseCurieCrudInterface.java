package org.alliancegenome.curation_api.base.interfaces;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseCurieCrudInterface<E extends BaseEntity> {

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
	@Tag(name = "Database Search Endpoints")
	@JsonView(View.FieldsAndLists.class)
	public SearchResponse<E> find(
			@DefaultValue("0") @QueryParam("page") Integer page,
			@DefaultValue("10") @QueryParam("limit") Integer limit,
			@RequestBody HashMap<String, Object> params);
	
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Endpoints")
	@JsonView({View.FieldsAndLists.class})
	public SearchResponse<E> search(
			@DefaultValue("0") @QueryParam("page") Integer page,
			@DefaultValue("10") @QueryParam("limit") Integer limit,
			@RequestBody HashMap<String, Object> params);
	
	@GET
	@Path("/reindex")
	@Tag(name = "Reindex Endpoints")
	public void reindex(
		@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("10000") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("4") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("7200") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel
	);

}

