package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.interfaces.crud.TransgenicToolCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.TransgenicToolExecutor;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.services.TransgenicToolService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. Mirrors ConstructCrudController. */
@RequestScoped
public class TransgenicToolCrudController extends SubmittedObjectCrudController<TransgenicToolService, TransgenicTool, TransgenicToolDTO, TransgenicToolDAO> implements TransgenicToolCrudInterface {

	@Inject TransgenicToolService transgenicToolService;
	@Inject TransgenicToolExecutor transgenicToolExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolService);
	}

	@Override
	public ObjectListResponse<TransgenicToolTransgenicToolAssociation> getTransgenicToolAssociations(Long id) {
		return transgenicToolService.getTransgenicToolAssociations(id);
	}

	// SCRUM-6655: the response is a fresh read made after the replacement's transaction, whose session is closed
	// by the time the response is written; outside a transaction the session lasts the request, as for a GET.
	@Override
	public ObjectListResponse<TransgenicToolTransgenicToolAssociation> updateTransgenicToolAssociations(Long id, List<TransgenicToolTransgenicToolAssociation> associations) {
		transgenicToolService.replaceTransgenicToolAssociations(id, associations);
		return transgenicToolService.getTransgenicToolAssociations(id);
	}

	@Override
	public APIResponse updateTransgenicTools(String dataProvider, List<TransgenicToolDTO> data) {
		return transgenicToolExecutor.runLoadApi(transgenicToolService, dataProvider, data);
	}

}
