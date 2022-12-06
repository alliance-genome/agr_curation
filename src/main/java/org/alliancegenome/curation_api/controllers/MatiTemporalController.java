package org.alliancegenome.curation_api.controllers;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.mati.Identifier;
import org.alliancegenome.curation_api.model.mati.IdentifiersRange;
import org.alliancegenome.curation_api.services.mati.MaTIService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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
		return maTIService.mintIdentifier(subdomain);
	}

	@POST
	public IdentifiersRange testMatiServiceMany(@HeaderParam("subdomain") String subdomain, @HeaderParam("value") String value) throws IOException {
		return maTIService.mintIdentifierRange(subdomain, value);
	}
}
