package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.interfaces.crud.TransgenicToolCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.TransgenicToolExecutor;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.TransgenicToolService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolCrudController extends SubmittedObjectCrudController<TransgenicToolService, TransgenicTool, TransgenicToolDTO, TransgenicToolDAO> implements TransgenicToolCrudInterface {

	@Inject
	TransgenicToolService transgenicToolService;
	@Inject
	TransgenicToolExecutor transgenicToolExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolService);
	}

	@Override
	public APIResponse updateTransgenicTools(String dataProvider, List<TransgenicToolDTO> annotations) {
		return transgenicToolExecutor.runLoadApi(transgenicToolService, dataProvider, annotations);
	}


}
