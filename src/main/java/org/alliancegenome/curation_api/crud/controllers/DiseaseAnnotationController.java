package org.alliancegenome.curation_api.crud.controllers;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.rest.DiseaseAnnotationRESTInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

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
    public ObjectResponse<DiseaseAnnotation> get(String idOrCurie) {
        try {
            Long idl = Long.parseLong(idOrCurie);
            return annotationService.get(idl);
        } catch (Exception e) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("curie", idOrCurie);
            SearchResponse<DiseaseAnnotation> results = annotationService.findByParams(null, params);
            
            ObjectResponse<DiseaseAnnotation> ret = new ObjectResponse<DiseaseAnnotation>();
            
            if(results.getTotalResults() > 0) {
                ret.setEntity(results.getResults().get(0));
            }
            return ret;
        }
    }
    
    @Override
    public ObjectResponse<DiseaseAnnotation> delete(String idOrCurie) {
        try {
            Long idl = Long.parseLong(idOrCurie);
            return annotationService.delete(idl);
        } catch (Exception e) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("curie", idOrCurie);
            SearchResponse<DiseaseAnnotation> results = annotationService.findByParams(null, params);
            
            ObjectResponse<DiseaseAnnotation> ret = new ObjectResponse<DiseaseAnnotation>();
            
            if(results.getTotalResults() > 0) {
                return annotationService.delete(results.getResults().get(0).getId());
            }
            return ret;
        }
    }

}
