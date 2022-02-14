package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationFmsCrudInterface extends BaseCurieCrudInterface<DiseaseAnnotation> {
    
    @POST
    @Path("/find")
    @Tag(name = "Database Search Endpoints")
    @JsonView(View.FieldsAndLists.class)
    public SearchResponse<DiseaseAnnotation> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
    @POST @Secured
    @Path("/bulk/{taxonID}/annotationFileFms")
    public String updateDiseaseAnnotations(@PathParam("taxonID") String taxonID, DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/zfinAnnotationFileFms")
    public String updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/mgiAnnotationFileFms")
    public String updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/rgdAnnotationFileFms")
    public String updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/fbAnnotationFileFms")
    public String updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/wbAnnotationFileFms")
    public String updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/humanAnnotationFileFms")
    public String updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

    @POST @Secured
    @Path("/bulk/sgdAnnotationFileFms")
    public String updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotationData);

}
