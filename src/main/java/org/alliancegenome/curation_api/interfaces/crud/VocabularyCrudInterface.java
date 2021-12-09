package org.alliancegenome.curation_api.interfaces.crud;


import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/vocabulary")
@Tag(name = "CRUD - Vocabulary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyCrudInterface extends BaseCrudInterface<Vocabulary> {

    @GET
    @Path("/{id}")
    @JsonView(View.VocabularyView.class)
    ObjectResponse<Vocabulary> get(@PathParam("id") Long id);
    
    @GET
    @Path("/{id}/terms")
    @JsonView(View.VocabularyTermView.class)
    ObjectListResponse<VocabularyTerm> getTerm(@PathParam("id") Long id);

    @POST
    @Path("/find")
    @JsonView(View.VocabularyView.class)
    @Tag(name = "Database Search Endpoints")
    public SearchResponse<Vocabulary> find(
            @DefaultValue("0") @QueryParam("page") Integer page,
            @DefaultValue("10") @QueryParam("limit") Integer limit,
            @RequestBody HashMap<String, Object> params);
    
}
