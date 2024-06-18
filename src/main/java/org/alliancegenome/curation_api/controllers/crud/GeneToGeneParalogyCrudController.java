package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneToGeneParalogyCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.ParalogyExecutor;
import org.alliancegenome.curation_api.services.GeneToGeneParalogyService;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyIngestFmsDTO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneParalogyCrudController extends BaseEntityCrudController<GeneToGeneParalogyService, GeneToGeneParalogy, GeneToGeneParalogyDAO> implements GeneToGeneParalogyCrudInterface {

	@Inject
	GeneToGeneParalogyService geneToGeneParalogyService;
	@Inject
	ParalogyExecutor paralogyExecutor;

	@Override @PostConstruct
	public void init() {
		setService(geneToGeneParalogyService);
	}

	@Override
	public APIResponse updateParalogy(String dataProvider, ParalogyIngestFmsDTO paralogyData) {
		return paralogyExecutor.runLoadApi(geneToGeneParalogyService, dataProvider, paralogyData.getData());
	}
	
}
