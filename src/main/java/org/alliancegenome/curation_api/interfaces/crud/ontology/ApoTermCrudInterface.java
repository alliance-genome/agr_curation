package org.alliancegenome.curation_api.interfaces.crud.ontology;

import org.alliancegenome.curation_api.interfaces.base.BaseOntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.APOTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/apoterm")
@Tag(name = "CRUD - Ontology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ApoTermCrudInterface extends BaseOntologyTermCrudInterface<APOTerm> {

}