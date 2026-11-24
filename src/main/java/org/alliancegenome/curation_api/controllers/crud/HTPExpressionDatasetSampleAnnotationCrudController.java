package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.HTPExpressionDatasetSampleAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.HTPExpressionDatasetSampleAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.HTPExpressionDatasetSampleAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetSampleAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class HTPExpressionDatasetSampleAnnotationCrudController extends BaseEntityCrudController<HTPExpressionDatasetSampleAnnotationService, HTPExpressionDatasetSampleAnnotation, HTPExpressionDatasetSampleAnnotationDAO> implements HTPExpressionDatasetSampleAnnotationCrudInterface {

	@Inject
	HTPExpressionDatasetSampleAnnotationService htpExpressionDatasetSampleAnnotationService;
	@Inject
	HTPExpressionDatasetSampleAnnotationExecutor htpExpressionDatasetSampleAnnotationExecutor;

	@Override
	@PostConstruct
	public void init() {
		setService(htpExpressionDatasetSampleAnnotationService);
	}

	@Override
	public APIResponse updateHTPExpressionDatasetSampleAnnotation(String dataProvider, HTPExpressionDatasetSampleAnnotationIngestFmsDTO htpDatasetSampleData) {
		return htpExpressionDatasetSampleAnnotationExecutor.runLoadApi(htpExpressionDatasetSampleAnnotationService, dataProvider, htpDatasetSampleData.getData());
	}

	// SCRUM-6715: direct LinkML DQM submission, alongside the FMS endpoint above. Mirrors
	// HTPExpressionDatasetAnnotationCrudController's own updateHTPExpressionDatasetAnnotations.
	@Override
	public APIResponse updateHTPExpressionDatasetSampleAnnotations(String dataProvider, List<HTPExpressionDatasetSampleAnnotationDTO> htpDatasetSampleData) {
		return htpExpressionDatasetSampleAnnotationExecutor.runLoadApi(htpExpressionDatasetSampleAnnotationService::upsert, dataProvider, htpDatasetSampleData);
	}

	// SCRUM-6534: the entity has no primaryExternalId/modInternalId (it extends AuditedObject, not
	// SubmittedObject), so the generic 3-field getByIdentifier throws a PathElementException for this
	// entity - look up by curie alone. The AGRKB curie (SCRUM-6463) is minted after create and
	// backfilled separately, so most rows still have none - fall back to the numeric DB id so every
	// sample has a working URL today, with curie taking over once minted.
	@Override
	public ObjectResponse<HTPExpressionDatasetSampleAnnotation> getByIdentifier(String identifier) {
		ObjectResponse<HTPExpressionDatasetSampleAnnotation> response = htpExpressionDatasetSampleAnnotationService.getByCurie(identifier);
		if (response.getEntity() == null) {
			try {
				response = htpExpressionDatasetSampleAnnotationService.getById(Long.valueOf(identifier));
			} catch (NumberFormatException e) {
				// identifier isn't a curie or a numeric id either - leave as not-found
			}
		}
		return response;
	}

}