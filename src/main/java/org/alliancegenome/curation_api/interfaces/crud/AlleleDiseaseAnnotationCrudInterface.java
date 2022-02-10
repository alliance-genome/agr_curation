package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele-disease-annotation")
@Tag(name = "CRUD - Allele Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<AlleleDiseaseAnnotation> {

    @GET
    @Path("/findBy/{curie}")
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
    
    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFile")
    public String updateAlleleDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<AlleleDiseaseAnnotationDTO> annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFile")
    public String updateZfinAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/mgiAnnotationFile")
    public String updateMgiAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/rgdAnnotationFile")
    public String updateRgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/fbAnnotationFile")
    public String updateFbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/wbAnnotationFile")
    public String updateWbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/humanAnnotationFile")
    public String updateHumanAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/sgdAnnotationFile")
    public String updateSgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
}
