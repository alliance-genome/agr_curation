package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneExecutor;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.GeneService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneCrudController extends SubmittedObjectCrudController<GeneService, Gene, GeneDTO, GeneDAO> implements GeneCrudInterface {

	@Inject
	GeneService geneService;

	@Inject
	GeneExecutor geneExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneService);
	}

	@Override
	public APIResponse updateGenes(String dataProvider, List<GeneDTO> geneData) {
		return geneExecutor.runLoad(dataProvider, geneData);
	}

}
