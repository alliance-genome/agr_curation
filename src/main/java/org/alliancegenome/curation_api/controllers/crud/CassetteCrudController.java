package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.interfaces.crud.CassetteCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.CassetteExecutor;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CassetteService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. Mirrors ConstructCrudController. */
@RequestScoped
public class CassetteCrudController extends SubmittedObjectCrudController<CassetteService, Cassette, CassetteDTO, CassetteDAO> implements CassetteCrudInterface {

	@Inject CassetteService cassetteService;
	@Inject CassetteExecutor cassetteExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteService);
	}

	// SCRUM-6746: the response is a fresh read made after the update's transaction. The update keeps
	// the cassette's components and associations as stored, so their lazy collections (evidence, notes,
	// the associated entities' lists) are not loaded by it, and its session is closed by the time the
	// response is written; outside a transaction the session lasts the request, as for a GET.
	@Override
	public ObjectResponse<Cassette> update(Cassette entity) {
		ObjectResponse<Cassette> response = cassetteService.update(entity);
		return cassetteService.getById(response.getEntity().getId());
	}

	@Override
	public APIResponse updateCassettes(String dataProvider, List<CassetteDTO> data) {
		return cassetteExecutor.runLoadApi(cassetteService, dataProvider, data);
	}

}
