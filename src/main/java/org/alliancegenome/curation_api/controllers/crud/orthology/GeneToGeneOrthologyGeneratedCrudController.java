package org.alliancegenome.curation_api.controllers.crud.orthology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.interfaces.crud.orthology.GeneToGeneOrthologyGeneratedCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.OrthologyExecutor;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;

@RequestScoped
public class GeneToGeneOrthologyGeneratedCrudController extends BaseEntityCrudController<GeneToGeneOrthologyGeneratedService, GeneToGeneOrthologyGenerated, GeneToGeneOrthologyGeneratedDAO> implements GeneToGeneOrthologyGeneratedCrudInterface {

	@Inject
	GeneToGeneOrthologyGeneratedService geneToGeneOrthologyGeneratedService;
	@Inject
	OrthologyExecutor orthologyExecutor;
	
	@Override
	@PostConstruct
	public void init() {
		setService(geneToGeneOrthologyGeneratedService);
	}

	@Override
	public APIResponse updateOrthology(OrthologyIngestFmsDTO orthologyData) {
		return orthologyExecutor.runLoad(orthologyData);
	}

}
