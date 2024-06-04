package org.alliancegenome.curation_api.interfaces.base;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseReindexControllerInterface {

	@GET
	@Path("/reindex")
	@Tag(name = "Reindex Endpoints")
	void reindex(
		@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("4") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);
}
