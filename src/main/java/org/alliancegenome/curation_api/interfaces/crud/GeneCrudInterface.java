package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/gene")
@Tag(name = "CRUD - Genes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneCrudInterface extends BaseCurieCrudInterface<Gene>, BaseDTOCrudControllerInterface<Gene, GeneDTO> {

	@Override
	@GET
	@Path("/{curie}")
	@JsonView(View.GeneView.class)
	public ObjectResponse<Gene> get(@PathParam("curie") String curie);

	@POST
	@Path("/bulk/{dataType}/genes")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateGenes(@PathParam("dataType") String dataType, List<GeneDTO> geneData);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.GeneView.class)
	public ObjectResponse<Gene> update(Gene entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.GeneView.class)
	public ObjectResponse<Gene> create(Gene entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Database Search Endpoints")
	@JsonView(View.GeneView.class)
	public SearchResponse<Gene> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Endpoints")
	@JsonView({ View.GeneView.class })
	public SearchResponse<Gene> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
