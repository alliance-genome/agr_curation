package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBbtTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/wbbtterm")
@Tag(name = "CRUD - Ontology - WBbt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WbbtTermCrudInterface extends BaseCurieCrudInterface<WBbtTerm> {

}