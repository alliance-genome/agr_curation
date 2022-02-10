package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AGMDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationCrudService;

@RequestScoped
public class AGMDiseaseAnnotationCrudController extends BaseCrudController<AGMDiseaseAnnotationCrudService, AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> implements AGMDiseaseAnnotationCrudInterface {

    @Inject
    AGMDiseaseAnnotationCrudService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }

    @Override
    public ObjectResponse<AGMDiseaseAnnotation> get(String uniqueId) {
        SearchResponse<AGMDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
        if(ret != null && ret.getTotalResults() == 1) {
            return new ObjectResponse<>(ret.getResults().get(0));
        } else {
            return new ObjectResponse<>();
        }
    }

    @Override
    public String updateAgmDiseaseAnnotations(String taxonID, List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad(taxonID, annotations);
        return "OK";
    }

    @Override
    public String updateZfinAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:7955", annotations);
        return "OK";
    }

    @Override
    public String updateMgiAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:10090", annotations);
        return "OK";
    }

    @Override
    public String updateRgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:10116", annotations);
        return "OK";
    }

    @Override
    public String updateFbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:7227", annotations);
        return "OK";
    }

    @Override
    public String updateWbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:6239", annotations);
        return "OK";
    }

    @Override
    public String updateHumanAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:9606", annotations);
        return "OK";
    }

    @Override
    public String updateSgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:559292", annotations);
        return "OK";
    }

}
