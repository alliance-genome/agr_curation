package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/gene-disease-annotation")
@Tag(name = "CRUD - Gene Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<GeneDiseaseAnnotation> {

    @GET
    @Path("/findBy/{curie}")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<GeneDiseaseAnnotation> get(@PathParam("curie") String curie);
    
    @POST
    @Path("/find")
    @Tag(name = "Database Search Endpoints")
    @JsonView(View.FieldsAndLists.class)
    public SearchResponse<GeneDiseaseAnnotation> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFile")
    public String updateGeneDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<GeneDiseaseAnnotationDTO> annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFile")
    public String updateZfinGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/mgiAnnotationFile")
    public String updateMgiGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/rgdAnnotationFile")
    public String updateRgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/fbAnnotationFile")
    public String updateFbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/wbAnnotationFile")
    public String updateWbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/humanAnnotationFile")
    public String updateHumanGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/sgdAnnotationFile")
    public String updateSgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
}
