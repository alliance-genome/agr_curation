package org.alliancegenome.curation_api.crud.controllers;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.RestErrorException;
import org.alliancegenome.curation_api.interfaces.rest.DiseaseAnnotationRESTInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class DiseaseAnnotationController extends BaseController<DiseaseAnnotationService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationRESTInterface {

    @Inject
    DiseaseAnnotationService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }

    @Override
    public ObjectResponse<DiseaseAnnotation> update(DiseaseAnnotation entity) {
        annotationService.validateAnnotation(entity);
        // assumes the incoming object is a complete object
        return super.update(entity);
    }

    public ObjectResponse<DiseaseAnnotation> get(Long id) {
        return annotationService.get(id);
    }

}
