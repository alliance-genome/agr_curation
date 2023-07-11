package org.alliancegenome.curation_api.interfaces.curationreports;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;
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

@Path("/curationreport")
@Tag(name = "Curation Report - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CurationReportCrudInterface extends BaseIdCrudInterface<CurationReport> {

	@GET
	@Path("/{id}")
	@JsonView(View.ReportHistory.class)
	public ObjectResponse<CurationReport> get(@PathParam("id") Long id);

	@GET
	@Path("/restart/{id}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<CurationReport> restartReport(@PathParam("id") Long id);

}
