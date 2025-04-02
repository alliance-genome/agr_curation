package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseCurieObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExternalDatabaseReference;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/externaldatabasereference")
@Tag(name = "CRUD - External Database References")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExternalDatabaseReferenceCrudInterface extends BaseCurieObjectCrudInterface<ExternalDatabaseReference> {

}
