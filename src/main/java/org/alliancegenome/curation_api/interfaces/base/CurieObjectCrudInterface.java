package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.model.entities.base.CurieObject;
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
public interface CurieObjectCrudInterface<E extends CurieObject> extends BaseIdCrudInterface<E> {

	@GET
	@Path("/{curie}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> get(@PathParam("curie") String curie);

	@DELETE
	@Path("/{curie}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> delete(@PathParam("curie") String curie);

}
