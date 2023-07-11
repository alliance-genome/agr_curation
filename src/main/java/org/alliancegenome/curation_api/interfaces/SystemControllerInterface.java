package org.alliancegenome.curation_api.interfaces;

import java.util.Map;

import org.alliancegenome.curation_api.response.ObjectResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/system")
@Tag(name = "System Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SystemControllerInterface {

	@GET
	@Path("/reindexeverything")
	public void reindexEverything(@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects, @DefaultValue("10000") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo, @DefaultValue("4") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout, @DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);

	@GET
	@Path("/sitesummary")
	public ObjectResponse<Map<String, Object>> getSiteSummary();
	
	@GET
	@Path("/updatedauniqueids")
	public void updateDiseaseAnnotationUniqueIds();

	// TODO remove once SCRUM-3037 resolved
	@GET
	@Path("/reset-da-data-providers")
	public void resetDiseaseAnnotationDataProviders();
	
	@GET
	@Path("/updaterefreshindex")
	public void updateRefreshIntervalOnAllIndexes();

}
