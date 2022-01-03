package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationCrudInterface extends BaseCurieCrudInterface<DiseaseAnnotation> {

    @POST
    @Path("/bulk/{taxonID}/annotationFile")
    public String updateDiseaseAnnotations(@PathParam("taxonID") String taxonID, DiseaseAnnotationMetaDataDTO annotationData);

    @POST
    @Path("/bulk/zfinAnnotationFile")
    public String updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
    @POST
    @Path("/bulk/mgiAnnotationFile")
    public String updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
    @POST
    @Path("/bulk/rgdAnnotationFile")
    public String updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
    @POST
    @Path("/bulk/fbAnnotationFile")
    public String updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
    @POST
    @Path("/bulk/wbAnnotationFile")
    public String updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
    @POST
    @Path("/bulk/humanAnnotationFile")
    public String updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
    @POST
    @Path("/bulk/sgdAnnotationFile")
    public String updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData);
    
}
