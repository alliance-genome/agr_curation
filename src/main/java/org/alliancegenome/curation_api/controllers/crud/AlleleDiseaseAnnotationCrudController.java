package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;

@RequestScoped
public class AlleleDiseaseAnnotationCrudController extends BaseCrudController<AlleleDiseaseAnnotationService, AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDAO> implements AlleleDiseaseAnnotationCrudInterface {

	@Inject AlleleDiseaseAnnotationService annotationService;
	@Inject AlleleDiseaseAnnotationExecutor alleleDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(annotationService);
	}
	
	@Override
	public ObjectResponse<AlleleDiseaseAnnotation> get(String uniqueId) {
		SearchResponse<AlleleDiseaseAnnotation> ret = findByField("uniqueId", uniqueId);
		if(ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<AlleleDiseaseAnnotation>();
		}
	}
	
	@Override
	public APIResponse updateAlleleDiseaseAnnotations(String taxonID, List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad(taxonID, annotations);
	}

	@Override
	public APIResponse updateZfinAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:7955", annotations);
	}

	@Override
	public APIResponse updateMgiAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:10090", annotations);
	}

	@Override
	public APIResponse updateRgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:10116", annotations);
	}

	@Override
	public APIResponse updateFbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:7227", annotations);
	}

	@Override
	public APIResponse updateWbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:6239", annotations);
	}

	@Override
	public APIResponse updateHumanAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:9606", annotations);
	}

	@Override
	public APIResponse updateSgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoad("NCBITaxon:559292", annotations);
	}

}
