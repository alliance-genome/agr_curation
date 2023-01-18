package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/resourcedescriptor")
@Tag(name = "CRUD - Resource Descriptor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ResourceDescriptorCrudInterface extends BaseIdCrudInterface<ResourceDescriptor> {

	@GET
	@Path("/{id}")
	@JsonView(View.ResourceDescriptorView.class)
	ObjectResponse<ResourceDescriptor> get(@PathParam("id") Long id);

}
