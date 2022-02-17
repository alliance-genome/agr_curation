package org.alliancegenome.curation_api.interfaces.crud.bulkloads;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/bulkloadfilehistory")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkLoadFileHistoryCrudInterface extends BaseIdCrudInterface<BulkLoadFileHistory> {

    @GET
    @Path("/{id}")
    @JsonView(View.BulkLoadFileHistory.class)
    public ObjectResponse<BulkLoadFileHistory> get(@PathParam("id") Long id);
    
}
