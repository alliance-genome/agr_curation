package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.interfaces.crud.CassetteCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.CassetteExecutor;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.CassetteService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteCrudController extends SubmittedObjectCrudController<CassetteService, Cassette, CassetteDTO, CassetteDAO> implements CassetteCrudInterface {

	@Inject
	CassetteService cassetteService;
	@Inject
	CassetteExecutor cassetteExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteService);
	}

	@Override
	public APIResponse updateCassettes(String dataProvider, List<CassetteDTO> annotations) {
		return cassetteExecutor.runLoadApi(cassetteService, dataProvider, annotations);
	}


}
