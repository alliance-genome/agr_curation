package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
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


}
