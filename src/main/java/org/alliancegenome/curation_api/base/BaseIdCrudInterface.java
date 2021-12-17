package org.alliancegenome.curation_api.base;

import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseIdCrudInterface<E extends BaseEntity> {

    @POST //@Secured
    @Path("/")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<E> create(E entity);

    @GET
    @Path("/{id}")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<E> get(@PathParam("curie") Long id);
    
    @PUT //@Secured
    @Path("/")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<E> update(E entity);

    @DELETE //@Secured
    @Path("/{id}")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<E> delete(@PathParam("curie") Long id);
    
    @POST
    @Path("/find")
    @Tag(name = "Database Search Endpoints")
    @JsonView(View.FieldsOnly.class)
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
        @DefaultValue("4") @QueryParam("threads") Integer threads,
        @DefaultValue("0") @QueryParam("indexAmount") Integer indexAmount 
    );
    
}

