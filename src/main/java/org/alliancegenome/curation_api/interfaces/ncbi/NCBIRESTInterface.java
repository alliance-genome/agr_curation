package org.alliancegenome.curation_api.interfaces.ncbi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.ingest.NCBITaxonResponseDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/entrez/eutils")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "DataFile Endpoints")
public interface NCBIRESTInterface {

	@GET
	@Path("/esummary.fcgi")
	public NCBITaxonResponseDTO getTaxonFromNCBI(@QueryParam("db") String db, @QueryParam("retmode") String retmode, @QueryParam("id") String id);
}
