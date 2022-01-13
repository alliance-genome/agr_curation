package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
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

}
