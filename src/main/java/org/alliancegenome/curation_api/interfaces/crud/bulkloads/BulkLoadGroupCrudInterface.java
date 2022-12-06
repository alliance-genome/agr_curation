package org.alliancegenome.curation_api.interfaces.crud.bulkloads;


import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/bulkloadgroup")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkLoadGroupCrudInterface extends BaseIdCrudInterface<BulkLoadGroup> {

}
