package org.alliancegenome.curation_api.interfaces.crud;


import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele-disease-annotation")
@Tag(name = "CRUD - Allele Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<AlleleDiseaseAnnotation> {

    @GET
    @Path("/findBy/{uniqueId}")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<AlleleDiseaseAnnotation> get(@PathParam("uniqueId") String uniqueId);
    
    @PUT @Secured
    @Path("/")
    @JsonView(View.FieldsAndLists.class)
    public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation entity);
    
    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateAlleleDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<AlleleDiseaseAnnotationDTO> annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateZfinAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/mgiAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateMgiAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/rgdAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateRgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/fbAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateFbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/wbAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateWbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/humanAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateHumanAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/sgdAnnotationFile")
    @JsonView(View.FieldsAndLists.class)
    public APIResponse updateSgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
}
