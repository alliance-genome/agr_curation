package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.AntibodyDAO;
import org.alliancegenome.curation_api.interfaces.crud.AntibodyCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AntibodyExecutor;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.ingest.dto.AntibodyDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AntibodyService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AntibodyCrudController extends SubmittedObjectCrudController<AntibodyService, Antibody, AntibodyDTO, AntibodyDAO> implements AntibodyCrudInterface {

	@Inject
	AntibodyService antibodyService;
	@Inject
	AntibodyExecutor antibodyExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(antibodyService);
	}

	@Override
	public APIResponse updateAntibodies(String dataProvider, List<AntibodyDTO> annotations) {
		return antibodyExecutor.runLoadApi(antibodyService, dataProvider, annotations);
	}

}
