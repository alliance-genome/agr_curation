package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AGMDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationCrudService;

@RequestScoped
public class AGMDiseaseAnnotationCrudController extends BaseController<AGMDiseaseAnnotationCrudService, AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> implements AGMDiseaseAnnotationCrudInterface {

    @Inject
    AGMDiseaseAnnotationCrudService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }


}
