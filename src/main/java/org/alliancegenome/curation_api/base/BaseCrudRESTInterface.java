package org.alliancegenome.curation_api.base;

import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.fasterxml.jackson.annotation.JsonView;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseCrudRESTInterface<E extends BaseEntity> {

    @POST //@Secured
    @Path("/")
    public ObjectResponse<E> create(E entity);

    @GET
    @Path("/{curie}")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<E> get(@PathParam("curie") String id);
    
    @PUT //@Secured
    @Path("/")
    public ObjectResponse<E> update(E entity);

    @DELETE //@Secured
    @Path("/{curie}")
    public ObjectResponse<E> delete(@PathParam("curie") String curie);

    @POST
    @Path("/find")
    @JsonView(View.FieldsOnly.class)
    public SearchResponse<E> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
    @POST
    @Path("/search")
    @JsonView(View.FieldsOnly.class)
    public SearchResponse<E> search(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
    @GET
    @Path("/reindex")
    public void reindex();
    
}

