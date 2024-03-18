package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.interfaces.crud.AffectedGenomicModelCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AgmExecutor;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AffectedGenomicModelCrudController extends SubmittedObjectCrudController<AffectedGenomicModelService, AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> implements AffectedGenomicModelCrudInterface {

	@Inject
	AffectedGenomicModelService affectedGenomicModelService;

	@Inject
	AgmExecutor agmExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(affectedGenomicModelService);
	}

	@Override
	public APIResponse updateAGMs(String dataProvider, List<AffectedGenomicModelDTO> agmData) {
		return agmExecutor.runLoad(dataProvider, agmData);
	}

}
