package org.alliancegenome.curation_api.interfaces.curationreports;


import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReportGroup;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/curationreportgroup")
@Tag(name = "Curation Report - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CurationReportGroupCrudInterface extends BaseIdCrudInterface<CurationReportGroup> {

}
