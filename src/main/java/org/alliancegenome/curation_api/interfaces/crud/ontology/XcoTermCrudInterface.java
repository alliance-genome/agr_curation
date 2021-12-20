package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/xcoterm")
@Tag(name = "CRUD - Ontology - XCO")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface XcoTermCrudInterface extends BaseIdCrudInterface<XcoTerm> {

}