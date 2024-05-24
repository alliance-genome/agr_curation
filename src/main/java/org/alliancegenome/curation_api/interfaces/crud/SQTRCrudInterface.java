package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.SQTR;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SQTRFmsDTO;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@GraphQLApi
@Path("/sqtr")
@Tag(name = "CRUD - SQTR")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SQTRCrudInterface extends BaseSubmittedObjectCrudInterface<SQTR>, BaseUpsertControllerInterface<SQTR, SQTRFmsDTO> {

}
