package org.alliancegenome.curation_api.interfaces.crud.orthology;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/orthology")
@Tag(name = "CRUD - Orthology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneToGeneOrthologyCrudInterface extends BaseIdCrudInterface<GeneToGeneOrthology> {

}