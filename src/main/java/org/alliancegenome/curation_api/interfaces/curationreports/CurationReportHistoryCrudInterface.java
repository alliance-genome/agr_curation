package org.alliancegenome.curation_api.interfaces.curationreports;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/curationreporthistory")
@Tag(name = "Curation Report - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CurationReportHistoryCrudInterface extends BaseIdCrudInterface<CurationReportHistory> {
}