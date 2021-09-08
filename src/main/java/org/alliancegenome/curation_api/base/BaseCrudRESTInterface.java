package org.alliancegenome.curation_api.base;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.fasterxml.jackson.annotation.JsonView;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseCrudRESTInterface<E extends BaseEntity> {

    @POST //@Secured
    @Path("/")
    public E create(E entity);

    @GET //@Secured
    @Path("/{id}")
    @JsonView(View.FieldsOnly.class)
    public E get(@PathParam("id") String id);

    @GET
    @Path("/view/{id}")
    @JsonView(View.FieldsOnly.class)
    public E get(@PathParam("id") Long id);

    @PUT //@Secured
    @Path("/")
    public E update(E entity);

    @DELETE //@Secured
    @Path("/{id}")
    public E delete(@PathParam("id") String id);

    @GET //@Secured
    @Path("/all")
    @JsonView(View.FieldsOnly.class)
    public SearchResults<E> getAll(
        @DefaultValue("0") @QueryParam("page") Integer page,
        @DefaultValue("10") @QueryParam("limit") Integer limit
    );
    
    @POST //@Secured
    @Path("/find")
    @JsonView(View.FieldsOnly.class)
    public List<E> find(@RequestBody HashMap<String, Object> params);
    
    @POST //@Secured
    @Path("/search")
    @JsonView(View.FieldsOnly.class)
    public SearchResults<E> search(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
    @GET
    @Path("/reindex")
    public void reindex();
    
}

