package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/vocabulary")
@Tag(name = "CRUD - Vocabulary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyCrudInterface extends BaseIdCrudInterface<Vocabulary> {

	@Operation(summary = "Get vocabulary by ID", description = "Retrieve a single vocabulary by its database ID")
	@GET
	@Path("/{id}")
	@JsonView(CurationView.VocabularyView.class)
	ObjectResponse<Vocabulary> getById(@PathParam("id") Long id);

	@Operation(summary = "Get terms for vocabulary", description = "Retrieve all vocabulary terms belonging to this vocabulary")
	@GET
	@Path("/{id}/terms")
	@JsonView(CurationView.VocabularyTermView.class)
	ObjectListResponse<VocabularyTerm> getTerms(@PathParam("id") Long id);

	@Operation(summary = "Find vocabulary by name", description = "Retrieve a vocabulary by its name")
	@GET
	@Path("/findBy/{name}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<Vocabulary> findByName(@PathParam("name") String name);

}
