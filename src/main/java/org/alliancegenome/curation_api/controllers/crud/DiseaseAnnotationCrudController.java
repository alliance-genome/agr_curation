package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.DiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

@RequestScoped
public class DiseaseAnnotationCrudController extends BaseEntityCrudController<DiseaseAnnotationService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationCrudInterface {

	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(diseaseAnnotationService);
	}
	
	@Override
	public ObjectResponse<DiseaseAnnotation> get(String uniqueId) {
		SearchResponse<DiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
		if(ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<DiseaseAnnotation>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<DiseaseAnnotation>();
		}
	}
}
