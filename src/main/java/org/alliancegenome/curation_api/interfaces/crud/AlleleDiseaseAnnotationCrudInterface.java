package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele-disease-annotation")
@Tag(name = "CRUD - Allele Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDiseaseAnnotationCrudInterface extends BaseCrudInterface<AlleleDiseaseAnnotation> {

    @GET
    @Path("/{curie}")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<AlleleDiseaseAnnotation> get(@PathParam("curie") String curie);
    
    @POST
    @Path("/find")
    @JsonView(View.FieldsAndLists.class)
    @Tag(name = "Database Search Endpoints")
    public SearchResponse<AlleleDiseaseAnnotation> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
}
