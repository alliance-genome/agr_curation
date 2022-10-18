package org.alliancegenome.curation_api.controllers;

import org.alliancegenome.curation_api.model.mati.Identifier;
import org.alliancegenome.curation_api.model.mati.IdentifiersRange;
import org.alliancegenome.curation_api.services.mati.MaTIService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/mati")
@Tag(name = "Mati Test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class MatiTemporalController {
	@Inject
	MaTIService maTIService;

	@PUT
	public Identifier testMatiService(@HeaderParam("subdomain") String subdomain) throws IOException {
		return  maTIService.mintIdentifier(subdomain);
	}

	@POST
	public IdentifiersRange testMatiServiceMany(@HeaderParam("subdomain") String subdomain, @HeaderParam("value") String value) throws IOException {
		return maTIService.mintIdentifierRange(subdomain, value);
	}
}
