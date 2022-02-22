package org.alliancegenome.curation_api.interfaces.crud;


import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.*;
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
    
    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFile")
    public APIResponse updateGeneDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<GeneDiseaseAnnotationDTO> annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFile")
    public APIResponse updateZfinGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/mgiAnnotationFile")
    public APIResponse updateMgiGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/rgdAnnotationFile")
    public APIResponse updateRgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/fbAnnotationFile")
    public APIResponse updateFbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/wbAnnotationFile")
    public APIResponse updateWbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/humanAnnotationFile")
    public APIResponse updateHumanGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/sgdAnnotationFile")
    public APIResponse updateSgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotationData);
}
