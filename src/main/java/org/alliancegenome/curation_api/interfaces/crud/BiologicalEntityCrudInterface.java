package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/biologicalentity")
@Tag(name = "CRUD - Biological Entities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BiologicalEntityCrudInterface extends BaseCurieCrudInterface<BiologicalEntity> {

}