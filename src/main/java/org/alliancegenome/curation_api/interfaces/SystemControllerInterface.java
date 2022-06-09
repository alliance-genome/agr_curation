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
		@DefaultValue("1") @QueryParam("threads") Integer threads,
		@DefaultValue("0") @QueryParam("indexAmount") Integer indexAmount,
		@DefaultValue("20000") @QueryParam("batchSize") Integer batchSize
	);

	@GET
	@Path("/sitesummary")
	public ObjectResponse<Map<String, Object>> getSiteSummary();

}
