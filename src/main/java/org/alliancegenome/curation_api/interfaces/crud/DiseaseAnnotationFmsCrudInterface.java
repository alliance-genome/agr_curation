package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationFmsCrudInterface extends BaseIdCrudInterface<DiseaseAnnotation> {
    
    @POST
    @Path("/bulk/{taxonID}/annotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateDiseaseAnnotations(@PathParam("taxonID") String taxonID, DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/zfinAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/mgiAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/rgdAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/fbAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/wbAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/humanAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/sgdAnnotationFileFms")
    @JsonView({View.FieldsAndLists.class})
    public APIResponse updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

}
