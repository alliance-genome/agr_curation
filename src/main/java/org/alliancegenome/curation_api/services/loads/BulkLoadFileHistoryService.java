package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class BulkLoadFileHistoryService extends BaseEntityCrudService<BulkLoadFileHistory, BulkLoadFileHistoryDAO> {

	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileHistoryDAO);
	}

	@Transactional
	public Response download(Long id) {
		JsonArray jsonArray = new JsonArray();
		BulkLoadFileHistory bulkLoadFileHistory = bulkLoadFileHistoryDAO.find(id);
		for(BulkLoadFileException exception : bulkLoadFileHistory.getExceptions()){
			JsonObject object = new JsonObject();
			object.put("message", exception.getException().getMessage());
			JsonObject data = new JsonObject(exception.getException().getJsonObject());
			object.put("jsonObject", data);
			jsonArray.add(object);
		}
		Response.ResponseBuilder response = Response.ok(jsonArray.toString());
		response.header("Content-Disposition", "attachment; filename=\"" + bulkLoadFileHistory.getBulkLoadFile().getBulkLoad().getName().replace( " ", "_") + "_exceptions.json\"");
		response.type(MediaType.APPLICATION_OCTET_STREAM);
		return response.build();
	}

}
