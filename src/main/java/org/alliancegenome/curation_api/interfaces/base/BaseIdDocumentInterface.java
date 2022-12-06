package org.alliancegenome.curation_api.interfaces.base;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.document.base.BaseDocument;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseIdDocumentInterface<E extends BaseDocument> {

	@GET
	@Path("/{id}")
	@JsonView(View.FieldsOnly.class)
	public ObjectResponse<E> get(@PathParam("id") String id);

	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Endpoints")
	@JsonView({View.FieldsAndLists.class})
	public SearchResponse<E> search(
			@DefaultValue("0") @QueryParam("page") Integer page,
			@DefaultValue("10") @QueryParam("limit") Integer limit,
			@RequestBody HashMap<String, Object> params);
	
}

