package org.alliancegenome.curation_api.interfaces.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/doterm")
@Tag(name = "Disease Terms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DoTermRESTInterface extends BaseCrudRESTInterface<DOTerm> {

}