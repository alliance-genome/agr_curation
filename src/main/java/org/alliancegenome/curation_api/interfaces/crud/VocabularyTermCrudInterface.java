package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.alliancegenome.curation_api.view.CurationView.VocabularyTermView;
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
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/vocabularyterm")
@Tag(name = "CRUD - VocabularyTerm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyTermCrudInterface extends BaseIdCrudInterface<VocabularyTerm> {

	@Override
	@POST
	@Path("/search")
	@JsonView(CurationView.VocabularyTermView.class)
	@Tag(name = "Elastic Search Browsing Endpoints")
	SearchResponse<VocabularyTerm> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@GET
	@Path("/{id}")
	@JsonView(CurationView.VocabularyTermView.class)
	ObjectResponse<VocabularyTerm> getById(@PathParam("id") Long id);

	@Operation(summary = "Get term in vocabulary", description = "Retrieve a specific vocabulary term by vocabulary name and term name")
	@GET
	@Path("/findBy")
	@JsonView(CurationView.VocabularyTermView.class)
	ObjectResponse<VocabularyTerm> getTermInVocabulary(@QueryParam("vocabularyName") String vocabularyName, @QueryParam("termName") String termName);

	@Operation(summary = "Get term in vocabulary term set", description = "Retrieve a specific vocabulary term by vocabulary term set name and term name")
	@GET
	@Path("/findInSet")
	@JsonView(CurationView.VocabularyTermView.class)
	ObjectResponse<VocabularyTerm> getTermInVocabularyTermSet(@QueryParam("vocabularyTermSetName") String vocabularyTermSetName, @QueryParam("termName") String termName);

	@Operation(summary = "Update", description = "Update for vocabulary term")
	@PUT
	@Path("/")
	@JsonView(CurationView.VocabularyTermUpdate.class)
	ObjectResponse<VocabularyTerm> update(VocabularyTerm entity);

	@Operation(summary = "Create vocabulary term", description = "Create a new vocabulary term entity")
	@POST
	@Path("/")
	@JsonView(VocabularyTermView.class)
	ObjectResponse<VocabularyTerm> create(VocabularyTerm entity);
}
