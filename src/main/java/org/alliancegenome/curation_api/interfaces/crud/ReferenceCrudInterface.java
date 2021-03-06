package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;


@Path("/reference")
@Tag(name = "CRUD - Reference")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ReferenceCrudInterface extends BaseCurieCrudInterface<Reference> {
	
	@GET
	@Path("/sync")
	public void synchroniseReferences();
	
	@GET
	@Path("/sync/{curie}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<Reference> synchroniseReference(@PathParam("curie") String curie);
}
