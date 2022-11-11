package org.alliancegenome.curation_api.interfaces.curationreports;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/curationreporthistory")
@Tag(name = "Curation Report - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CurationReportHistoryCrudInterface extends BaseIdCrudInterface<CurationReportHistory> {

	@GET
	@Path("/{id}")
	@JsonView(View.BulkLoadFileHistory.class)
	public ObjectResponse<CurationReportHistory> get(@PathParam("id") Long id);
	
}
