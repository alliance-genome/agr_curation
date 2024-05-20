package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
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

@Path("/allele")
@Tag(name = "CRUD - Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleCrudInterface extends BaseSubmittedObjectCrudInterface<Allele>, BaseUpsertControllerInterface<Allele, AlleleDTO> {

	@POST
	@Path("/bulk/{dataProvider}/alleles")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateAlleles(@PathParam("dataProvider") String dataProvider, List<AlleleDTO> alleleData);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(View.AlleleDetailView.class)
	public ObjectResponse<Allele> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.AlleleView.class)
	public ObjectResponse<Allele> update(Allele entity);
	
	@PUT
	@Path("/updateDetail")
	@JsonView(View.AlleleDetailView.class)
	public ObjectResponse<Allele> updateDetail(Allele entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.AlleleView.class)
	public ObjectResponse<Allele> create(Allele entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.AlleleView.class)
	public SearchResponse<Allele> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.AlleleView.class })
	public SearchResponse<Allele> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
