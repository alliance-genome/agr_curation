package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseCurieObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/reference")
@Tag(name = "CRUD - Reference")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ReferenceCrudInterface extends BaseCurieObjectCrudInterface<Reference> {

	@GET
	@Path("/sync")
	void synchroniseReferences();

	@GET
	@Path("/sync/{id}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<Reference> synchroniseReference(@PathParam("id") Long id);
}
