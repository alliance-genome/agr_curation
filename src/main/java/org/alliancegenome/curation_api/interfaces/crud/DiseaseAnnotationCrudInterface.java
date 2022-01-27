package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationCrudInterface extends BaseCurieCrudInterface<DiseaseAnnotation> {

    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFile")
    public String updateDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<DiseaseAnnotationDTO> annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFile")
    public String updateZFinDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/mgiAnnotationFile")
    public String updateMgiDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/rgdAnnotationFile")
    public String updateRgdDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/fbAnnotationFile")
    public String updateFBDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/wbAnnotationFile")
    public String updateWBDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/humanAnnotationFile")
    public String updateHUMANDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
    @POST @Secured
    @Path("/bulk/sgdAnnotationFile")
    public String updateSGDDiseaseAnnotations(List<DiseaseAnnotationDTO> annotationData);
    
}
