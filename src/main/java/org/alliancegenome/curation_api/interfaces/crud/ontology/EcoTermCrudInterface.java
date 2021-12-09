package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/ecoterm")
@Tag(name = "CRUD - Ontology - ECO")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface EcoTermCrudInterface extends BaseCrudInterface<EcoTerm> {

}