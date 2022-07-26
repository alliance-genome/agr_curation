package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.AGMDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AgmDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;

@RequestScoped
public class AGMDiseaseAnnotationCrudController extends BaseDTOCrudController<AGMDiseaseAnnotationService, AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO, AGMDiseaseAnnotationDAO> implements AGMDiseaseAnnotationCrudInterface {

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
	public ObjectResponse<AGMDiseaseAnnotation> createAgmDiseaseAnnotation(AGMDiseaseAnnotationDTO annotation) throws ObjectUpdateException {
		return annotationService.upsertDTO(annotation);
	}

	@Override
	public APIResponse updateZfinAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:7955", annotations);
	}

	@Override
	public APIResponse updateMgiAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:10090", annotations);
	}

	@Override
	public APIResponse updateRgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:10116", annotations);
	}

	@Override
	public APIResponse updateFbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:7227", annotations);
	}

	@Override
	public APIResponse updateWbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:6239", annotations);
	}

	@Override
	public APIResponse updateHumanAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:9606", annotations);
	}

	@Override
	public APIResponse updateSgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotations) {
		return agmDiseaseAnnotationExecutor.runLoad("NCBITaxon:559292", annotations);
	}

}
