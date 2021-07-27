package org.alliancegenome.curation_api.rest.interfaces;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/allele")
@Tag(name = "Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleRESTInterface extends BaseCrudRESTInterface<Allele> {

}
