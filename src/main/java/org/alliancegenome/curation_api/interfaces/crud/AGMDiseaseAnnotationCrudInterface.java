package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/agm-disease-annotation")
@Tag(name = "CRUD - AGM Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AGMDiseaseAnnotationCrudInterface extends BaseCrudInterface<AGMDiseaseAnnotation> {

    @GET
    @Path("/{curie}")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<AGMDiseaseAnnotation> get(@PathParam("curie") String curie);
    
    @POST
    @Path("/find")
    @Tag(name = "Database Search Endpoints")
    @JsonView(View.FieldsAndLists.class)
    public SearchResponse<AGMDiseaseAnnotation> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
}
