package org.alliancegenome.curation_api.interfaces.crud.bulkloads;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/bulkloadfile")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkLoadFileCrudInterface extends BaseIdCrudInterface<BulkLoadFile> {

	@GET
	@Path("/restart/{id}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<BulkLoadFile> restartLoadFile(@PathParam("id") Long id);
}
