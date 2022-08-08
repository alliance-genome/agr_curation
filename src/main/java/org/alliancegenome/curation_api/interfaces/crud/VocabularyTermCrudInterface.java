package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

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
	public SearchResponse<VocabularyTerm> search(
			@DefaultValue("0") @QueryParam("page") Integer page,
			@DefaultValue("10") @QueryParam("limit") Integer limit,
			@RequestBody HashMap<String, Object> params);
	
	@Override
	@GET
	@Path("/{id}")
	@JsonView(View.VocabularyTermView.class)
	public ObjectResponse<VocabularyTerm> get(@PathParam("id") Long id);
	
	@GET
	@Path("/{name}/{vocabulary}")
	@JsonView(View.VocabularyTermView.class)
	public ObjectResponse<VocabularyTerm> getTermInVocabulary(@PathParam("name") String name, @PathParam("vocabulary") String vocabulary);

	@PUT
	@Path("/")
	@JsonView(View.VocabularyTermUpdate.class)
	public ObjectResponse<VocabularyTerm> update(VocabularyTerm entity);
}
