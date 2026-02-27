package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
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
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/construct")
@Tag(name = "CRUD - Constructs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructCrudInterface extends BaseSubmittedObjectCrudInterface<Construct>, BaseUpsertControllerInterface<Construct, ConstructDTO> {

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.ConstructView.class)
	ObjectResponse<Construct> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.ConstructView.class)
	ObjectResponse<Construct> update(Construct entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.ConstructView.class)
	ObjectResponse<Construct> create(Construct entity);

	@Operation(summary = "Bulk load construct data", description = "Bulk load construct records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/constructs")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateConstructs(@PathParam("dataProvider") String dataProvider, List<ConstructDTO> constructData);

}
