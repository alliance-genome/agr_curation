package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationCrudService;

@RequestScoped
public class GeneDiseaseAnnotationCrudController extends BaseCrudController<GeneDiseaseAnnotationCrudService, GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> implements GeneDiseaseAnnotationCrudInterface {

    @Inject
    GeneDiseaseAnnotationCrudService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }

    @Override
    public ObjectResponse<GeneDiseaseAnnotation> get(String uniqueId) {
        SearchResponse<GeneDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
        if(ret != null && ret.getTotalResults() == 1) {
            return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
        } else {
            return new ObjectResponse<GeneDiseaseAnnotation>();
        }
    }

    @Override
    public String updateGeneDiseaseAnnotations(String taxonID, List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad(taxonID, annotations);
        return "OK";
    }

    @Override
    public String updateZfinGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:7955", annotations);
        return "OK";
    }

    @Override
    public String updateMgiGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:10090", annotations);
        return "OK";
    }

    @Override
    public String updateRgdGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:10116", annotations);
        return "OK";
    }

    @Override
    public String updateFbGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:7227", annotations);
        return "OK";
    }

    @Override
    public String updateWbGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:6239", annotations);
        return "OK";
    }

    @Override
    public String updateHumanGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:9606", annotations);
        return "OK";
    }

    @Override
    public String updateSgdGeneDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        annotationService.runLoad("NCBITaxon:559292", annotations);
        return "OK";
    }
}
