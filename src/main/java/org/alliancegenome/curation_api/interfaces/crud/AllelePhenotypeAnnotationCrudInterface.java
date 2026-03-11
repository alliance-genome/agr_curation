package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
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

@Path("/allele-phenotype-annotation")
@Tag(name = "CRUD - Allele Phenotype Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AllelePhenotypeAnnotationCrudInterface extends BaseIdCrudInterface<AllelePhenotypeAnnotation> {

	@Operation(summary = "Get allele phenotype annotation by identifier", description = "Retrieve a single allele phenotype annotation by its identifier")
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<AllelePhenotypeAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.PhenotypeAnnotationView.class)
	ObjectResponse<AllelePhenotypeAnnotation> update(AllelePhenotypeAnnotation entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.PhenotypeAnnotationView.class)
	ObjectResponse<AllelePhenotypeAnnotation> create(AllelePhenotypeAnnotation entity);

}
