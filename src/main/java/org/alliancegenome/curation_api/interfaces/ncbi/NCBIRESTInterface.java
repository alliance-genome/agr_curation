package org.alliancegenome.curation_api.interfaces.ncbi;

import org.alliancegenome.curation_api.model.ingest.NCBITaxonResponseDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/entrez/eutils")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "DataFile Endpoints")
public interface NCBIRESTInterface {

	@GET
	@Path("/esummary.fcgi")
	public NCBITaxonResponseDTO getTaxonFromNCBI(@QueryParam("db") String db, @QueryParam("retmode") String retmode, @QueryParam("id") String id);
}
