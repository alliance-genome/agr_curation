package org.alliancegenome.curation_api.base;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseCrudRESTInterface<E> {

	@POST //@Secured
	@Path("/")
	public E create(E entity);

	@GET //@Secured
	@Path("/{id}")
	@JsonView(View.FieldsOnly.class)
	public E get(@PathParam("id") String id);

	@PUT //@Secured
	@Path("/")
	public E update(E entity);

	@DELETE //@Secured
	@Path("/{id}")
	public E delete(@PathParam("id") String id);

	@GET //@Secured
	@Path("/all")
	@JsonView(View.FieldsOnly.class)
	public List<E> getAll();
	
	@GET
	@Path("/reindex")
	public void reindex();
	
}

