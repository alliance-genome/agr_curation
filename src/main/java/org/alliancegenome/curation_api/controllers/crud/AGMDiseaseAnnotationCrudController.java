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
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;

@RequestScoped
public class AGMDiseaseAnnotationCrudController extends BaseDTOCrudController<AGMDiseaseAnnotationService, AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO, AGMDiseaseAnnotationDAO>
	implements AGMDiseaseAnnotationCrudInterface {

	@Inject
	AGMDiseaseAnnotationService annotationService;

	@Inject
	AgmDiseaseAnnotationExecutor agmDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(annotationService);
	}

	@Override
	public ObjectResponse<AGMDiseaseAnnotation> get(String uniqueId) {
		SearchResponse<AGMDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
		if (ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<>();
		}
	}

	@Override
	public APIResponse updateAgmDiseaseAnnotations(String taxonID, List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad(taxonID, annotations);
	}

	@Override
	public APIResponse updateZfinAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Danio rerio", annotations);
	}

	@Override
	public APIResponse updateMgiAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Mus musculus", annotations);
	}

	@Override
	public APIResponse updateRgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Rattus norvegicus", annotations);
	}

	@Override
	public APIResponse updateFbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Drosophila melanogaster", annotations);
	}

	@Override
	public APIResponse updateWbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Caenorhabditis elegans", annotations);
	}

	@Override
	public APIResponse updateHumanAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Homo sapiens", annotations);
	}

	@Override
	public APIResponse updateSgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("Saccharomyces cerevisiae", annotations);
	}

}
