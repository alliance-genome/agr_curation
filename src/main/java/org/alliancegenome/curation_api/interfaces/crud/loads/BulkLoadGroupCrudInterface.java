package org.alliancegenome.curation_api.interfaces.crud.loads;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/bulkloadgroup")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkLoadGroupCrudInterface extends BaseCrudInterface<BulkLoadGroup> {

}
