package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AGMDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AgmDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AGMDiseaseAnnotationCrudController extends BaseDTOCrudController<AGMDiseaseAnnotationService, AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO, AGMDiseaseAnnotationDAO> implements AGMDiseaseAnnotationCrudInterface {

	@Inject
	AGMDiseaseAnnotationService agmDiseaseAnnotationService;
	@Inject
	AgmDiseaseAnnotationExecutor agmDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmDiseaseAnnotationService);
	}

	@Override
	public APIResponse updateAgmDiseaseAnnotations(String dataProvider, List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad(dataProvider, annotations);
	}
}
