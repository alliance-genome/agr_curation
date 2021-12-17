package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/zecoterm")
@Tag(name = "CRUD - Ontology - ZECO")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ZecoTermCrudInterface extends BaseIdCrudInterface<ZecoTerm> {

}