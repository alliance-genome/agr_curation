package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/resourcedescriptor")
@Tag(name = "CRUD - Resource Descriptor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ResourceDescriptorCrudInterface extends BaseIdCrudInterface<ResourceDescriptor> {

	@Override
	@GET
	@Path("/{id}")
	@JsonView(CurationView.ResourceDescriptorView.class)
	ObjectResponse<ResourceDescriptor> getById(@PathParam("id") Long id);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.ResourceDescriptorView.class)
	ObjectResponse<ResourceDescriptor> create(ResourceDescriptor entity);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.ResourceDescriptorView.class)
	ObjectResponse<ResourceDescriptor> update(ResourceDescriptor entity);

}
