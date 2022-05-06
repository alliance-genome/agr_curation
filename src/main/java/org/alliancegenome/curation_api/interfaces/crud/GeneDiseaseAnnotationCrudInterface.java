package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/gene-disease-annotation")
@Tag(name = "CRUD - Gene Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<GeneDiseaseAnnotation> {

    @GET
    @Path("/findBy/{uniqueId}")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<GeneDiseaseAnnotation> get(@PathParam("uniqueId") String uniqueId);
    
    @PUT
    @Path("/")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation entity);
    
    @POST
    @Path("/bulk/{taxonID}/annotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateGeneDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<GeneDiseaseAnnotationDTO> annotationData);

    @POST
    @Path("/bulk/zfinAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateZfinGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST
    @Path("/bulk/mgiAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateMgiGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST
    @Path("/bulk/rgdAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateRgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST
    @Path("/bulk/fbAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateFbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST
    @Path("/bulk/wbAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateWbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST
    @Path("/bulk/humanAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateHumanGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST
    @Path("/bulk/sgdAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateSgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
}
