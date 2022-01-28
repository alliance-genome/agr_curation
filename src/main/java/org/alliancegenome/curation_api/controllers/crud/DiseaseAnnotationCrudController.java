package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.DiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

@RequestScoped
public class DiseaseAnnotationCrudController extends BaseCrudController<DiseaseAnnotationService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationCrudInterface {

    @Inject
    DiseaseAnnotationService diseaseAnnotationService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(diseaseAnnotationService);
    }

    @Override
    public String updateDiseaseAnnotations(String taxonID, List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad(taxonID, annotations);
        return "OK";
    }

    @Override
    public String updateZFinDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:7955", annotations);
        return "OK";
    }

    @Override
    public String updateMgiDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:10090", annotations);
        return "OK";
    }

    @Override
    public String updateRgdDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:10116", annotations);
        return "OK";
    }

    @Override
    public String updateFBDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:7227", annotations);
        return "OK";
    }

    @Override
    public String updateWBDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:6239", annotations);
        return "OK";
    }

    @Override
    public String updateHUMANDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:9606", annotations);
        return "OK";
    }

    @Override
    public String updateSGDDiseaseAnnotations(List<DiseaseAnnotationDTO> annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:559292", annotations);
        return "OK";
    }

}
