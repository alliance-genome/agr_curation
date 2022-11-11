package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseOntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/ontologyterm")
@Tag(name = "CRUD - Ontology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OntologyTermCrudInterface extends BaseOntologyTermCrudInterface<OntologyTerm> {

}