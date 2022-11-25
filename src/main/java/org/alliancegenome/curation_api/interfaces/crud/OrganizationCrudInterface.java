package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/organization")
@Tag(name = "CRUD - Organization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OrganizationCrudInterface extends BaseCurieCrudInterface<Organization> {
	
}
