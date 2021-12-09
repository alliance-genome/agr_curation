package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationCrudService;

@RequestScoped
public class GeneDiseaseAnnotationCrudController extends BaseController<GeneDiseaseAnnotationCrudService, GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> implements GeneDiseaseAnnotationCrudInterface {

    @Inject
    GeneDiseaseAnnotationCrudService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }


}
