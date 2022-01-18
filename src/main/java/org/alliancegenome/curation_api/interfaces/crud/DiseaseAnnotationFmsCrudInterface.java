package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationFmsCrudInterface extends BaseCurieCrudInterface<DiseaseAnnotation> {

    @POST
    @Path("/bulk/{taxonID}/annotationFileFms")
    public String updateDiseaseAnnotations(@PathParam("taxonID") String taxonID, DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/zfinAnnotationFileFms")
    public String updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/mgiAnnotationFileFms")
    public String updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/rgdAnnotationFileFms")
    public String updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/fbAnnotationFileFms")
    public String updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/wbAnnotationFileFms")
    public String updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/humanAnnotationFileFms")
    public String updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST
    @Path("/bulk/sgdAnnotationFileFms")
    public String updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

}
