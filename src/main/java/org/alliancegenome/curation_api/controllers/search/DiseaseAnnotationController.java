package org.alliancegenome.curation_api.controllers.search;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.search.DiseaseAnnotationInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

@RequestScoped
public class DiseaseAnnotationController extends BaseCrudController<DiseaseAnnotationService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationInterface {

    @Inject DiseaseAnnotationService diseaseAnnotationService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(diseaseAnnotationService);
    }

}
