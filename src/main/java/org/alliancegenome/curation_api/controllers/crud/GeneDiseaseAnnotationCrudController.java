package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneDiseaseAnnotationCrudController extends BaseDTOCrudController<GeneDiseaseAnnotationService, GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO, GeneDiseaseAnnotationDAO>
	implements GeneDiseaseAnnotationCrudInterface {

	@Inject
	GeneDiseaseAnnotationService geneDiseaseAnnotationService;
	@Inject
	GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(geneDiseaseAnnotationService);
	}

	@Override
	public APIResponse updateGeneDiseaseAnnotations(String dataProvider, List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad(dataProvider, annotations);
	}
}
