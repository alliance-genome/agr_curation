package org.alliancegenome.curation_api.interfaces.crud.orthology;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/orthologycurated")
@Tag(name = "CRUD - Orthology")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneToGeneOrthologyCuratedCrudInterface extends BaseIdCrudInterface<GeneToGeneOrthologyCurated> {

}