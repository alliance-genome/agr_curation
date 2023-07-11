package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AGMDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AgmDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;

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
