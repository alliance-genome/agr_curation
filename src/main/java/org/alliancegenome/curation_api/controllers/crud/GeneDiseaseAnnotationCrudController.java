package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;

@RequestScoped
public class GeneDiseaseAnnotationCrudController extends BaseCrudController<GeneDiseaseAnnotationService, GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> implements GeneDiseaseAnnotationCrudInterface {

    @Inject GeneDiseaseAnnotationService annotationService;
    
    @Inject GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;

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
    public APIResponse updateGeneDiseaseAnnotations(String taxonID, List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad(taxonID, annotations);
    }

    @Override
    public APIResponse updateZfinGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:7955", annotations);
    }

    @Override
    public APIResponse updateMgiGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:10090", annotations);
    }

    @Override
    public APIResponse updateRgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:10116", annotations);
    }

    @Override
    public APIResponse updateFbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:7227", annotations);
    }

    @Override
    public APIResponse updateWbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:6239", annotations);
    }

    @Override
    public APIResponse updateHumanGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:9606", annotations);
    }

    @Override
    public APIResponse updateSgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
        return geneDiseaseAnnotationExecutor.runLoad("NCBITaxon:559292", annotations);
    }

}
