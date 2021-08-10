package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

public class DiseaseAnnotationService extends BaseService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
		
	}

}
