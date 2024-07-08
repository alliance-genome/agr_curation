package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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

@GraphQLApi
@Path("/gene")
@Tag(name = "CRUD - Genes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneCrudInterface extends BaseSubmittedObjectCrudInterface<Gene>, BaseUpsertControllerInterface<Gene, GeneDTO> {

	@Override
	@GET
	@Query("api_gene_get")
	@Path("/{identifierString}")
	@JsonView(View.GeneDetailView.class)
	ObjectResponse<Gene> getByIdentifier(@PathParam("identifierString") String identifierString);

	@POST
	@Path("/bulk/{dataProvider}/genes")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateGenes(@PathParam("dataProvider") String dataProvider, List<GeneDTO> geneData);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.GeneView.class)
	ObjectResponse<Gene> update(Gene entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.GeneView.class)
	ObjectResponse<Gene> create(Gene entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.GeneView.class)
	SearchResponse<Gene> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.GeneView.class })
	SearchResponse<Gene> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
