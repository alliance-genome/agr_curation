package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
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

@Path("/vocabularytermset")
@Tag(name = "CRUD - VocabularyTermSet")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyTermSetCrudInterface extends BaseIdCrudInterface<VocabularyTermSet> {

	@Operation(summary = "Get vocabulary term set by ID", description = "Retrieve a single vocabulary term set by its database ID")
	@GET
	@Path("/{id}")
	@JsonView(CurationView.VocabularyTermSetView.class)
	ObjectResponse<VocabularyTermSet> getById(@PathParam("id") Long id);

	@Operation(summary = "Get terms for vocabulary term set", description = "Retrieve all vocabulary terms belonging to this vocabulary term set")
	@GET
	@Path("/{id}/terms")
	@JsonView(CurationView.VocabularyTermSetView.class)
	ObjectListResponse<VocabularyTerm> getTerms(@PathParam("id") Long id);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.VocabularyTermSetView.class)
	ObjectResponse<VocabularyTermSet> update(VocabularyTermSet entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.VocabularyTermSetView.class)
	ObjectResponse<VocabularyTermSet> create(VocabularyTermSet entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.VocabularyTermSetView.class)
	SearchResponse<VocabularyTermSet> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.VocabularyTermSetView.class })
	SearchResponse<VocabularyTermSet> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);
}
