package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseCurieObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.Reference;
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

@Path("/reference")
@Tag(name = "CRUD - Reference")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ReferenceCrudInterface extends BaseCurieObjectCrudInterface<Reference> {

	@Operation(summary = "Synchronise all references", description = "Synchronise all references with external sources")
	@GET
	@Path("/sync")
	void synchroniseReferences();

	@Operation(summary = "Synchronise reference", description = "Synchronise a single reference with external sources")
	@GET
	@Path("/sync/{id}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<Reference> synchroniseReference(@PathParam("id") Long id);
}
