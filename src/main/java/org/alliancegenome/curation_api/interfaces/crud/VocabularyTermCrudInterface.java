package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.alliancegenome.curation_api.view.View.VocabularyTermView;
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

@Path("/vocabularyterm")
@Tag(name = "CRUD - VocabularyTerm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyTermCrudInterface extends BaseIdCrudInterface<VocabularyTerm> {

	@Override
	@POST
	@Path("/search")
	@JsonView(View.VocabularyTermView.class)
	@Tag(name = "Elastic Search Endpoints")
	public SearchResponse<VocabularyTerm> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody HashMap<String, Object> params);

	@Override
	@GET
	@Path("/{id}")
	@JsonView(View.VocabularyTermView.class)
	public ObjectResponse<VocabularyTerm> get(@PathParam("id") Long id);

	@GET
	@Path("/findBy")
	@JsonView(View.VocabularyTermView.class)
	public ObjectResponse<VocabularyTerm> getTermInVocabulary(@QueryParam("vocabularyName") String vocabularyName, @QueryParam("termName") String termName);

	@GET
	@Path("/findInSet")
	@JsonView(View.VocabularyTermView.class)
	public ObjectResponse<VocabularyTerm> getTermInVocabularyTermSet(@QueryParam("vocabularyTermSetName") String vocabularyTermSetName, @QueryParam("termName") String termName);

	@PUT
	@Path("/")
	@JsonView(View.VocabularyTermUpdate.class)
	public ObjectResponse<VocabularyTerm> update(VocabularyTerm entity);

	@POST
	@Path("/")
	@JsonView(VocabularyTermView.class)
	public ObjectResponse<VocabularyTerm> create(VocabularyTerm entity);
}
