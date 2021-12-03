package org.alliancegenome.curation_api.interfaces.crud.ontology;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/zfaterm")
@Tag(name = "Ontology - ZFA")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ZfaTermRESTInterface extends BaseCrudRESTInterface<ZfaTerm> {

}