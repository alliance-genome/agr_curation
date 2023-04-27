package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectListResponse;
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

@Path("/vocabularytermset")
@Tag(name = "CRUD - VocabularyTermSet")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyTermSetCrudInterface extends BaseIdCrudInterface<VocabularyTermSet> {

	@GET
	@Path("/{id}")
	@JsonView(View.VocabularyTermSetView.class)
	ObjectResponse<VocabularyTermSet> get(@PathParam("id") Long id);

	@GET
	@Path("/{id}/terms")
	@JsonView(View.VocabularyTermSetView.class)
	ObjectListResponse<VocabularyTerm> getTerms(@PathParam("id") Long id);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.VocabularyTermSetView.class)
	public ObjectResponse<VocabularyTermSet> update(VocabularyTermSet entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.VocabularyTermSetView.class)
	public ObjectResponse<VocabularyTermSet> create(VocabularyTermSet entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Database Search Endpoints")
	@JsonView(View.VocabularyTermSetView.class)
	public SearchResponse<VocabularyTermSet> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Endpoints")
	@JsonView({ View.VocabularyTermSetView.class })
	public SearchResponse<VocabularyTermSet> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody HashMap<String, Object> params);
}
