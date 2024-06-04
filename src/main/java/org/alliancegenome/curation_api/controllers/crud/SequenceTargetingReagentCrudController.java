package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneCrudInterface;
import org.alliancegenome.curation_api.interfaces.crud.SequenceTargetingReagentCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneExecutor;
import org.alliancegenome.curation_api.jobs.executors.SequenceTargetingReagentExecutor;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.SequenceTargetingReagentService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SequenceTargetingReagentCrudController extends BaseEntityCrudController<SequenceTargetingReagentService, SequenceTargetingReagent, SequenceTargetingReagentDAO> implements SequenceTargetingReagentCrudInterface {

	@Inject
	SequenceTargetingReagentService sqtrService;

	@Inject
	SequenceTargetingReagentExecutor sqtrExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(sqtrService);
	}

	@Override
	public APIResponse updateSequenceTargetingReagent(String dataProvider, SequenceTargetingReagentIngestFmsDTO sqtrFmsDTO) {
		return sqtrExecutor.runLoadApi(sqtrService, dataProvider, sqtrFmsDTO.getData());
	}

}
