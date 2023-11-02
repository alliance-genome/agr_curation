package org.alliancegenome.curation_api.interfaces.crud.orthology;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/orthologycurated")
@Tag(name = "CRUD - Orthology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneToGeneOrthologyCuratedCrudInterface extends BaseIdCrudInterface<GeneToGeneOrthologyCurated> {

}