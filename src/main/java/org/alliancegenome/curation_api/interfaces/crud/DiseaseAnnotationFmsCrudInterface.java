package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationFmsCrudInterface extends BaseCurieCrudInterface<DiseaseAnnotation> {
    
    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFileFms")
    public APIResponse updateDiseaseAnnotations(@PathParam("taxonID") String taxonID, DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFileFms")
    public APIResponse updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/mgiAnnotationFileFms")
    public APIResponse updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/rgdAnnotationFileFms")
    public APIResponse updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/fbAnnotationFileFms")
    public APIResponse updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/wbAnnotationFileFms")
    public APIResponse updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/humanAnnotationFileFms")
    public APIResponse updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/sgdAnnotationFileFms")
    public APIResponse updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

}
