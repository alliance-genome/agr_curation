package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

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
	
	@GET
	@Path("/findBy/{name}")
	@JsonView(View.VocabularyTermSetView.class)
	public ObjectResponse<VocabularyTermSet> findByName(@PathParam("name") String name);
}
