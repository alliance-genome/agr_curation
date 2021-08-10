package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.rest.DiseaseAnnotationRESTInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

@RequestScoped
public class DiseaseAnnotationController extends BaseController<DiseaseAnnotationService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationRESTInterface {

	@Inject DiseaseAnnotationService diseaseAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(diseaseAnnotationService);
	}

}
