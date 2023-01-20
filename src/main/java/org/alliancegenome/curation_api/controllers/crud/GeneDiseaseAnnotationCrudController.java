package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;

@RequestScoped
public class GeneDiseaseAnnotationCrudController extends BaseDTOCrudController<GeneDiseaseAnnotationService, GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO, GeneDiseaseAnnotationDAO>
	implements GeneDiseaseAnnotationCrudInterface {

	@Inject
	GeneDiseaseAnnotationService annotationService;

	@Inject
	GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(annotationService);
	}

	@Override
	public ObjectResponse<GeneDiseaseAnnotation> get(String uniqueId) {
		SearchResponse<GeneDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
		if (ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<GeneDiseaseAnnotation>();
		}
	}

	@Override
	public APIResponse updateGeneDiseaseAnnotations(String taxonID, List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad(taxonID, annotations);
	}

	@Override
	public APIResponse updateZfinGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Danio rerio", annotations);
	}

	@Override
	public APIResponse updateMgiGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Mus musculus", annotations);
	}

	@Override
	public APIResponse updateRgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Rattus norvegicus", annotations);
	}

	@Override
	public APIResponse updateFbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Drosophila melanogaster", annotations);
	}

	@Override
	public APIResponse updateWbGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Caenorhabditis elegans", annotations);
	}

	@Override
	public APIResponse updateHumanGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Homo sapiens", annotations);
	}

	@Override
	public APIResponse updateSgdGeneDiseaseAnnotations(List<GeneDiseaseAnnotationDTO> annotations) {
		return geneDiseaseAnnotationExecutor.runLoad("Saccharomyces cerevisiae", annotations);
	}

}
