package org.alliancegenome.curation_api.interfaces.crud.bulkloads;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/bulkurlload")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkURLLoadCrudInterface extends BaseIdCrudInterface<BulkURLLoad> {

	@GET
	@Path("/restart/{id}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<BulkURLLoad> restartLoad(@PathParam("id") Long id);
}
