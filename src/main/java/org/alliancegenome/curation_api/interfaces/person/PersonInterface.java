package org.alliancegenome.curation_api.interfaces.person;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.Operation;

import com.fasterxml.jackson.annotation.JsonView;


@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PersonInterface {
    @POST
    @Path("/")
    @Operation(hidden=true)
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<Person> create(Person entity);
}
