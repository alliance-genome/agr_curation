package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/experimental-condition")
@Tag(name = "CRUD - ExperimentalConditions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExperimentalConditionCrudInterface extends BaseCurieCrudInterface<ExperimentalCondition> {

}
