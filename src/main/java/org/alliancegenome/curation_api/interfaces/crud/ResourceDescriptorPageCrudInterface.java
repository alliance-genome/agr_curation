package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/resourcedescriptorpage")
@Tag(name = "CRUD - Resource Descriptor Page")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ResourceDescriptorPageCrudInterface extends BaseIdCrudInterface<ResourceDescriptorPage> {
	@Override
	@POST
	@Path("/search")
	@JsonView(View.ResourceDescriptorPageView.class)
	@Tag(name = "Elastic Search Endpoints")
	public SearchResponse<ResourceDescriptorPage> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit,
		@RequestBody HashMap<String, Object> params);
}
