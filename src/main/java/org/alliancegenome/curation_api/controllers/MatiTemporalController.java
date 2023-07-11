package org.alliancegenome.curation_api.controllers;

import java.io.IOException;

import org.alliancegenome.curation_api.model.mati.Identifier;
import org.alliancegenome.curation_api.model.mati.IdentifiersRange;
import org.alliancegenome.curation_api.services.mati.MaTIService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
