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
	public ObjectResponse<AGMDiseaseAnnotation> get(String identifier) {
		SearchResponse<AGMDiseaseAnnotation> ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<AGMDiseaseAnnotation>();
	}

	@Override
	public APIResponse updateAgmDiseaseAnnotations(String dataProvider, List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad(dataProvider, annotations);
	}
}
