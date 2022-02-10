package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationCrudService;

@RequestScoped
public class AlleleDiseaseAnnotationCrudController extends BaseCrudController<AlleleDiseaseAnnotationCrudService, AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDAO> implements AlleleDiseaseAnnotationCrudInterface {

    @Inject
    AlleleDiseaseAnnotationCrudService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }
    
    @Override
    public ObjectResponse<AlleleDiseaseAnnotation> get(String uniqueId) {
        SearchResponse<AlleleDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
        if(ret != null && ret.getTotalResults() == 1) {
            return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
        } else {
            return new ObjectResponse<AlleleDiseaseAnnotation>();
        }
    }
    
    @Override
    public String updateAlleleDiseaseAnnotations(String taxonID, List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad(taxonID, annotations);
        return "OK";
    }

    @Override
    public String updateZfinAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:7955", annotations);
        return "OK";
    }

    @Override
    public String updateMgiAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:10090", annotations);
        return "OK";
    }

    @Override
    public String updateRgdAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:10116", annotations);
        return "OK";
    }

    @Override
    public String updateFbAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:7227", annotations);
        return "OK";
    }

    @Override
    public String updateWbAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:6239", annotations);
        return "OK";
    }

    @Override
    public String updateHumanAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:9606", annotations);
        return "OK";
    }

    @Override
    public String updateSgdAlleleDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:559292", annotations);
        return "OK";
    }

}
