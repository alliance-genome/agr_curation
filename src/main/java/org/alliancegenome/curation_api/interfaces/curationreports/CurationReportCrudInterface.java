package org.alliancegenome.curation_api.interfaces.curationreports;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

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
