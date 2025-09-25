package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.controllers.base.BaseAnnotationDTOCrudController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneDiseaseAnnotationCrudController extends BaseAnnotationDTOCrudController<GeneDiseaseAnnotationService, GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO, GeneDiseaseAnnotationDAO>
		implements GeneDiseaseAnnotationCrudInterface {

	@Inject
	GeneDiseaseAnnotationService geneDiseaseAnnotationService;
	@Inject
	AGMDiseaseAnnotationService agmDiseaseAnnotationService;
	@Inject
	AlleleDiseaseAnnotationService alleleDiseaseAnnotationService;
	@Inject
	GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneDiseaseAnnotationService);
	}

	public APIResponse updateGeneDiseaseAnnotations(String dataProvider, List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoadApi(geneDiseaseAnnotationService, dataProvider, annotations);
	}

	public ObjectResponse<GeneDiseaseAnnotation> getByIdentifier(String identifierString) {
		return geneDiseaseAnnotationService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<Set<String>> geneDiseaseAnnotationMap() {
		Set<String> map = geneDiseaseAnnotationService.getGeneDiseaseAnnotation();
		Set<String> map2 = agmDiseaseAnnotationService.getGeneDiseaseAnnotation();
		Set<String> map3 = alleleDiseaseAnnotationService.getGeneDiseaseAnnotation();
		map.addAll(map2);
		map.addAll(map3);
		ObjectResponse<Set<String>> response = new ObjectResponse<>();
		response.setEntity(map);
		return response;
	}
}
