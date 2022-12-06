package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/agm-disease-annotation")
@Tag(name = "CRUD - AGM Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AGMDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<AGMDiseaseAnnotation>, BaseDTOCrudControllerInterface<AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO> {

	@GET
	@Path("/findBy/{uniqueId}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AGMDiseaseAnnotation> get(@PathParam("uniqueId") String uniqueId);

	@PUT
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation entity);

	@POST
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<AGMDiseaseAnnotation> create(AGMDiseaseAnnotation entity);

	@POST
	@Path("/bulk/{taxonID}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateAgmDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/zfinAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateZfinAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/mgiAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateMgiAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/rgdAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateRgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/fbAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateFbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/wbAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateWbAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/humanAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateHumanAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/sgdAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateSgdAgmDiseaseAnnotations(List<AGMDiseaseAnnotationDTO> annotationData);
}
