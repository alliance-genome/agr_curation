package org.alliancegenome.curation_api.interfaces.crud.bulkloads;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/bulkloadfilehistory")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkLoadFileHistoryCrudInterface extends BaseIdCrudInterface<BulkLoadFileHistory> {
	@Operation(summary = "Download bulk load file", description = "Download the file associated with a bulk load file history entry")
	@GET
	@Path("/{id}/download")
	@JsonView(CurationView.BulkLoadFileHistoryView.class)
	@Produces(MediaType.APPLICATION_JSON)
	Response download(@PathParam("id") Long id);

	@Operation(summary = "Restart bulk load", description = "Restart a bulk load process by its ID")
	@GET
	@Path("/restartload/{id}")
	@JsonView(CurationView.FieldsOnly.class)
	ObjectResponse<BulkLoad> restartBulkLoad(@PathParam("id") Long id);
	
	@Operation(summary = "Stop bulk load file history", description = "Stop processing of a bulk load file history entry")
	@GET
	@Path("/stoploadhistory/{id}")
	@JsonView(CurationView.FieldsOnly.class)
	ObjectResponse<BulkLoadFile> stopBulkLoadHistory(@PathParam("id") Long id);
	
	@Operation(summary = "Restart bulk load file history", description = "Restart processing of a bulk load file history entry")
	@GET
	@Path("/restartloadhistory/{id}")
	@JsonView(CurationView.FieldsOnly.class)
	ObjectResponse<BulkLoadFile> restartBulkLoadHistory(@PathParam("id") Long id);

}
