package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;

@RequestScoped
public class AlleleDiseaseAnnotationCrudController extends BaseDTOCrudController<AlleleDiseaseAnnotationService, AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO, AlleleDiseaseAnnotationDAO>
	implements AlleleDiseaseAnnotationCrudInterface {

	@Inject
	AlleleDiseaseAnnotationService annotationService;
	@Inject
	AlleleDiseaseAnnotationExecutor alleleDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(annotationService);
	}

	@Override
	public ObjectResponse<AlleleDiseaseAnnotation> get(String uniqueId) {
		SearchResponse<AlleleDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
		if (ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<AlleleDiseaseAnnotation>();
		}
	}

	@Override
	public APIResponse updateAlleleDiseaseAnnotations(String speciesName, List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad(speciesName, annotations);
	}

	@Override
	public APIResponse updateZfinAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Danio rerio", annotations);
	}

	@Override
	public APIResponse updateMgiAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Mus musculus", annotations);
	}

	@Override
	public APIResponse updateRgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Rattus norvegicus", annotations);
	}

	@Override
	public APIResponse updateFbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Drosophila melanogaster", annotations);
	}

	@Override
	public APIResponse updateWbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Caenorhabditis elegans", annotations);
	}

	@Override
	public APIResponse updateHumanAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Homo sapiens", annotations);
	}

	@Override
	public APIResponse updateSgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("Saccharomyces cerevisiae", annotations);
	}

}
