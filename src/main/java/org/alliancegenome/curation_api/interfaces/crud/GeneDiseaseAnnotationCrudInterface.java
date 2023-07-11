package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
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

@Path("/gene-disease-annotation")
@Tag(name = "CRUD - Gene Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<GeneDiseaseAnnotation>, BaseDTOCrudControllerInterface<GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO> {

	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<GeneDiseaseAnnotation> get(@PathParam("identifier") String identifier);

	@PUT
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation entity);

	@POST
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<GeneDiseaseAnnotation> create(GeneDiseaseAnnotation entity);

	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateGeneDiseaseAnnotations(@PathParam("dataProvider") String dataProvider, List<GeneDiseaseAnnotationDTO> annotationData);

}
