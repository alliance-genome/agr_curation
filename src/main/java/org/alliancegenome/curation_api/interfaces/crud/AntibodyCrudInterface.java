package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.ingest.dto.AntibodyDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
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

@Path("/antibody")
@Tag(name = "CRUD - Antibodies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AntibodyCrudInterface extends BaseSubmittedObjectCrudInterface<Antibody>, BaseUpsertControllerInterface<Antibody, AntibodyDTO> {

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<Antibody> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<Antibody> update(Antibody entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<Antibody> create(Antibody entity);

	@Operation(summary = "Bulk load antibody data", description = "Bulk load antibody records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/antibodies")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateAntibodies(@PathParam("dataProvider") String dataProvider, List<AntibodyDTO> antibodyData);

}
