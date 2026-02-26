package org.alliancegenome.curation_api.interfaces.base;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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
	@Operation(summary = "Reindex entities into OpenSearch", description = "Trigger a reindex of this entity type from the relational database into OpenSearch. Configurable batch size, threading, and limits.")
	void reindex(
		@Parameter(description = "Number of objects to load per batch") @DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@Parameter(description = "Number of IDs to fetch at a time (0 = default)") @DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@Parameter(description = "Maximum number of objects to index (0 = unlimited)") @DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@Parameter(description = "Number of threads for loading objects") @DefaultValue("4") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@Parameter(description = "Transaction timeout in seconds") @DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@Parameter(description = "Number of types to index in parallel") @DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);
}
