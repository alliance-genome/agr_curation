package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/materm")
@Tag(name = "CRUD - Ontology - MA")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MaTermCrudInterface extends BaseCrudInterface<MATerm> {

}