package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/** SCRUM-6535. Mirrors ConstructCrudInterface. */
@Path("/transgenic-tool")
@Tag(name = "CRUD - Transgenic Tools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TransgenicToolCrudInterface extends BaseSubmittedObjectCrudInterface<TransgenicTool>, BaseUpsertControllerInterface<TransgenicTool, TransgenicToolDTO> {

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.TransgenicToolView.class)
	SearchResponse<TransgenicTool> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.TransgenicToolView.class })
	SearchResponse<TransgenicTool> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(CurationView.TransgenicToolView.class)
	ObjectResponse<TransgenicTool> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.TransgenicToolView.class)
	ObjectResponse<TransgenicTool> update(TransgenicTool entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.TransgenicToolView.class)
	ObjectResponse<TransgenicTool> create(TransgenicTool entity);

	@Operation(summary = "Get transgenic tool associations for transgenic tool", description = "The associations this transgenic tool is the subject of")
	@GET
	@Path("/{id}/transgenic-tool-associations")
	@JsonView(CurationView.TransgenicToolTransgenicToolAssociationView.class)
	ObjectListResponse<TransgenicToolTransgenicToolAssociation> getTransgenicToolAssociations(@PathParam("id") Long id);

	@Operation(summary = "Replace transgenic tool associations for transgenic tool", description = "Replace the associations this transgenic tool is the subject of with the submitted list, deleting any that are omitted")
	@PUT
	@Path("/{id}/transgenic-tool-associations")
	@JsonView(CurationView.TransgenicToolTransgenicToolAssociationView.class)
	ObjectListResponse<TransgenicToolTransgenicToolAssociation> updateTransgenicToolAssociations(@PathParam("id") Long id, List<TransgenicToolTransgenicToolAssociation> associations);

	@Operation(summary = "Bulk load transgenic-tool data", description = "Bulk load transgenic-tool records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/transgenicTools")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateTransgenicTools(@PathParam("dataProvider") String dataProvider, List<TransgenicToolDTO> data);

}
