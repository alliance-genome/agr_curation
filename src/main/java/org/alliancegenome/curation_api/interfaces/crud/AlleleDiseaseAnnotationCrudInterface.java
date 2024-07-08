package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
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

@Path("/allele-disease-annotation")
@Tag(name = "CRUD - Allele Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<AlleleDiseaseAnnotation>, BaseUpsertControllerInterface<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO> {

	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<AlleleDiseaseAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	ObjectResponse<AlleleDiseaseAnnotation> create(AlleleDiseaseAnnotation entity);

	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateAlleleDiseaseAnnotations(@PathParam("dataProvider") String dataProvider, List<AlleleDiseaseAnnotationDTO> annotationData);

}
