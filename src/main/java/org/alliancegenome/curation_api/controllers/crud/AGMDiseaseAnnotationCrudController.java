package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AGMDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationCrudService;

@RequestScoped
public class AGMDiseaseAnnotationCrudController extends BaseCrudController<AGMDiseaseAnnotationCrudService, AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> implements AGMDiseaseAnnotationCrudInterface {

    @Inject
    AGMDiseaseAnnotationCrudService annotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(annotationService);
    }

    @Override
    public ObjectResponse<AGMDiseaseAnnotation> get(String uniqueId) {
        SearchResponse<AGMDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
        if(ret != null && ret.getTotalResults() == 1) {
            return new ObjectResponse<>(ret.getResults().get(0));
        } else {
            return new ObjectResponse<>();
        }
    }

}
