package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
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

@Path("/allele-phenotype-annotation")
@Tag(name = "CRUD - Allele Phenotype Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AllelePhenotypeAnnotationCrudInterface extends BaseIdCrudInterface<AllelePhenotypeAnnotation> {
    
    @GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AllelePhenotypeAnnotation> get(@PathParam("identifier") String identifier);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.PhenotypeAnnotationView.class)
	public ObjectResponse<AllelePhenotypeAnnotation> update(AllelePhenotypeAnnotation entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.PhenotypeAnnotationView.class)
	public ObjectResponse<AllelePhenotypeAnnotation> create(AllelePhenotypeAnnotation entity);


}
