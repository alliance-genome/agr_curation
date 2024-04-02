package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/construct")
@Tag(name = "CRUD - Constructs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructCrudInterface extends BaseSubmittedObjectCrudInterface<Construct>, BaseUpsertControllerInterface<Construct, ConstructDTO> {

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(View.ConstructView.class)
	public ObjectResponse<Construct> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.ConstructView.class)
	public ObjectResponse<Construct> update(Construct entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.ConstructView.class)
	public ObjectResponse<Construct> create(Construct entity);

	@POST
	@Path("/bulk/{dataProvider}/constructs")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateConstructs(@PathParam("dataProvider") String dataProvider, List<ConstructDTO> constructData);

}
