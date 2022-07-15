package org.alliancegenome.curation_api.interfaces;

import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.response.ObjectResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/system")
@Tag(name = "System Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SystemControllerInterface {

	@GET
	@Path("/reindexeverything")
	public void reindexEverything(
		@DefaultValue("1") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("200") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("7200") @QueryParam("transactionTimeout") Integer transactionTimeout
	);

	@GET
	@Path("/sitesummary")
	public ObjectResponse<Map<String, Object>> getSiteSummary();

}
