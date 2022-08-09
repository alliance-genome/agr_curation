package org.alliancegenome.curation_api.interfaces.crud.ontology;

import org.alliancegenome.curation_api.interfaces.base.BaseOntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/atpterm")
@Tag(name = "CRUD - Ontology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AtpTermCrudInterface extends BaseOntologyTermCrudInterface<ATPTerm> {

}