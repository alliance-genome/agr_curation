package org.alliancegenome.curation_api.interfaces.rest;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/agm")
@Tag(name = "Affected Genomic Models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelRESTInterface extends BaseCrudRESTInterface<AffectedGenomicModel> {

}
