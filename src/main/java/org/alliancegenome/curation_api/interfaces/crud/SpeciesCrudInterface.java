package org.alliancegenome.curation_api.interfaces.crud;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/species")
@Tag(name = "CRUD - Species")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SpeciesCrudInterface extends BaseIdCrudInterface<Species> {
}
