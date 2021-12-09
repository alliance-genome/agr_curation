package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/zfaterm")
@Tag(name = "CRUD - Ontology - ZFA")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ZfaTermCrudInterface extends BaseCrudInterface<ZfaTerm> {

}