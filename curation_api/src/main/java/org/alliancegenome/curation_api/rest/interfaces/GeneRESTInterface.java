package org.alliancegenome.curation_api.rest.interfaces;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/gene")
@Tag(name = "Genes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneRESTInterface extends BaseCrudRESTInterface<Gene> {

	@GET //@Secured
	@Path("/by/{curie}")
	@JsonView(View.FieldsAndLists.class)
	public Gene getByCurie(@PathParam("curie") String curie);
}
