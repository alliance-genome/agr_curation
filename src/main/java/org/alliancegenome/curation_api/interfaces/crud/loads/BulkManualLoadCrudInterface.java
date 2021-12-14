package org.alliancegenome.curation_api.interfaces.crud.loads;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/bulkmanualload")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkManualLoadCrudInterface extends BaseCrudInterface<BulkManualLoad> {

}
