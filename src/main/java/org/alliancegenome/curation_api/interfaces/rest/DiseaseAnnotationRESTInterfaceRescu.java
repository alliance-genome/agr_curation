package org.alliancegenome.curation_api.interfaces.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * This class is only needed to be used with the rescu REST client library.
 * That library does not objectify the json correctly, i.e.
 * the E in the parent interface
 * public interface BaseCrudRESTInterface<E extends BaseEntity> {
 * is always of type BaseEntity. Looks like a bug in rescu.
 * Bug still present with version 2.1.0
 */

@Path("/disease-annotation")
@Tag(name = "Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationRESTInterfaceRescu extends DiseaseAnnotationRESTInterface {

    @GET
    @Path("/{curie}")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<DiseaseAnnotation> get(@PathParam("curie") String id);

    @POST
    @Path("/find")
    @JsonView(View.FieldsOnly.class)
    public SearchResponse<DiseaseAnnotation> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);

    @POST //@Secured
    @Path("/")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<DiseaseAnnotation> create(DiseaseAnnotation entity);

    @PUT //@Secured
    @Path("/")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<DiseaseAnnotation> update(DiseaseAnnotation entity);

    @DELETE //@Secured
    @Path("/{curie}")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<DiseaseAnnotation> delete(@PathParam("curie") String curie);

}
