package org.alliancegenome.curation_api.interfaces;

import java.util.Map;

import org.alliancegenome.curation_api.response.ObjectResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
	void reindexEverything(
		@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("8") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("4") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);

	@GET
	@Path("/sitesummary")
	ObjectResponse<Map<String, Object>> getSiteSummary();
	
	@GET
	@Path("/updatedauniqueids")
	void updateDiseaseAnnotationUniqueIds();

	// SCRUM-6078 backfill endpoint. Mints AGRKB curies for every
	// DiseaseAnnotation whose curie is currently NULL, in batches.
	// Idempotent. Remove after rollout on alpha/beta/prod.
	//
	// maxToMint caps the TOTAL number of annotations minted in a single call
	// so a cold full-table run cannot overwhelm the environment. 0 = no cap
	// (mint every NULL-curie annotation). Because the backfill is idempotent,
	// the endpoint can be called repeatedly with a bounded maxToMint to work
	// through the table in safe chunks.
	@GET
	@Path("/mintdacuries")
	void mintExistingDiseaseAnnotationCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	@DELETE
	@Path("/deletedUnusedConditionsAndExperiments")
	void deleteUnusedConditionsAndExperiments();

}
