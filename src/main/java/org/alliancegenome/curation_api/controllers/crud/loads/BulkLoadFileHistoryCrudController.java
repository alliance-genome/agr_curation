package org.alliancegenome.curation_api.controllers.crud.loads;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import jakarta.ws.rs.core.Response;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadFileHistoryCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkLoadFileHistoryService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;

@RequestScoped
public class BulkLoadFileHistoryCrudController extends BaseEntityCrudController<BulkLoadFileHistoryService, BulkLoadFileHistory, BulkLoadFileHistoryDAO> implements BulkLoadFileHistoryCrudInterface {

	@Inject
	BulkLoadFileHistoryService bulkLoadFileHistoryService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadFileHistoryService);
	}

	@Override
	public Response download(Long id) {
		return bulkLoadFileHistoryService.download(id);
	}

}
