package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
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
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/agm-phenotype-annotation")
@Tag(name = "CRUD - AGM Phenotype Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AGMPhenotypeAnnotationCrudInterface extends BaseIdCrudInterface<AGMPhenotypeAnnotation> {

	@Operation(summary = "Get AGM phenotype annotation by identifier", description = "Retrieve a single AGM phenotype annotation by its identifier")
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AGMPhenotypeAnnotation> getByIdentifier(@PathParam("identifier") String identifier);
	
	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.PhenotypeAnnotationView.class)
	ObjectResponse<AGMPhenotypeAnnotation> update(AGMPhenotypeAnnotation entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.PhenotypeAnnotationView.class)
	ObjectResponse<AGMPhenotypeAnnotation> create(AGMPhenotypeAnnotation entity);

	
}
