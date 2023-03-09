package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataType;
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
	public APIResponse updateAlleleDiseaseAnnotations(String dataType, List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad(BackendBulkDataType.getSpeciesNameFromDataType(dataType), annotations);
	}
}
