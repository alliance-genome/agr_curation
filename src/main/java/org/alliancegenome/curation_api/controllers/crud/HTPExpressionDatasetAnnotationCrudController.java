package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.HTPExpressionDatasetAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.HTPExpressionDatasetAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class HTPExpressionDatasetAnnotationCrudController extends BaseEntityCrudController<HTPExpressionDatasetAnnotationService, HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationDAO> implements HTPExpressionDatasetAnnotationCrudInterface {

	@Inject
	HTPExpressionDatasetAnnotationService htpExpressionDatasetAnnotationService;
	@Inject
	HTPExpressionDatasetAnnotationExecutor htpExpressionDatasetAnnotationExecutor;

	@Override
	@PostConstruct
	public void init() {
		setService(htpExpressionDatasetAnnotationService);
	}

	@Override
	public APIResponse updateHTPExpressionDatasetAnnotation(String dataProvider, HTPExpressionDatasetAnnotationIngestFmsDTO htpDatasetData) {
		return htpExpressionDatasetAnnotationExecutor.runLoadApi(dataProvider, htpDatasetData.getData());
	}

	// SCRUM-6174: the detail page's stable URL. The entity has no primaryExternalId/modInternalId
	// (it extends AuditedObject, not SubmittedObject), so the generic 3-field getByIdentifier throws
	// a PathElementException for this entity - look up by curie alone. The AGRKB curie (SCRUM-6463) is
	// minted after create and backfilled separately, so most rows still have none - fall back to the
	// numeric DB id so every dataset has a working URL today, with curie taking over once minted.
	@Override
	public ObjectResponse<HTPExpressionDatasetAnnotation> getByIdentifier(String identifier) {
		ObjectResponse<HTPExpressionDatasetAnnotation> response = htpExpressionDatasetAnnotationService.getByCurie(identifier);
		if (response.getEntity() == null) {
			try {
				response = htpExpressionDatasetAnnotationService.getById(Long.valueOf(identifier));
			} catch (NumberFormatException e) {
				// identifier isn't a curie or a numeric id either - leave as not-found
			}
		}
		return response;
	}

}