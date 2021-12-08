package org.alliancegenome.curation_api.interfaces.crud.ontology;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/chebiterm")
@Tag(name = "Ontology - ChEBI")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CHEBITermRESTInterface extends BaseCrudRESTInterface<CHEBITerm> {

}
