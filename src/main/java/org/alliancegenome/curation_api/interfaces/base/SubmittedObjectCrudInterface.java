package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubmittedObjectCrudInterface<E extends SubmittedObject> extends CurieObjectCrudInterface<E> {

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> get(@PathParam("identifierString") String identifierString);

	@Override
	@DELETE
	@Path("/{identifierString}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> delete(@PathParam("identifierString") String identifierString);

}
