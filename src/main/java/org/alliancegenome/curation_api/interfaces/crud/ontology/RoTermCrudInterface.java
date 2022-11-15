package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseOntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ROTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/roterm")
@Tag(name = "CRUD - Ontology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RoTermCrudInterface extends BaseOntologyTermCrudInterface<ROTerm> {

}