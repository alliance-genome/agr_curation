package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Person;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/person")
@Tag(name = "CRUD - Person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PersonCrudInterface extends BaseIdCrudInterface<Person> {
    
}
