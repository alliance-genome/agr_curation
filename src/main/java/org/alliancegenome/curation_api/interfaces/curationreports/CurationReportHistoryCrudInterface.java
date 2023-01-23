package org.alliancegenome.curation_api.interfaces.curationreports;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportHistory;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/curationreporthistory")
@Tag(name = "Curation Report - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CurationReportHistoryCrudInterface extends BaseIdCrudInterface<CurationReportHistory> {
}
