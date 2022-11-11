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
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele-disease-annotation")
@Tag(name = "CRUD - Allele Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<AlleleDiseaseAnnotation>, BaseDTOCrudControllerInterface<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO> {

	@GET
	@Path("/findBy/{uniqueId}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AlleleDiseaseAnnotation> get(@PathParam("uniqueId") String uniqueId);
	
	@PUT
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation entity);
	

	@POST
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<AlleleDiseaseAnnotation> create(AlleleDiseaseAnnotation entity);
	
	@POST
	@Path("/bulk/{taxonID}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateAlleleDiseaseAnnotations(@PathParam("taxonID") String taxonID, List<AlleleDiseaseAnnotationDTO> annotationData);

	@POST
	@Path("/bulk/zfinAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateZfinAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
	
	@POST
	@Path("/bulk/mgiAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateMgiAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
	
	@POST
	@Path("/bulk/rgdAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateRgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
	
	@POST
	@Path("/bulk/fbAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateFbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
	
	@POST
	@Path("/bulk/wbAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateWbAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
	
	@POST
	@Path("/bulk/humanAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateHumanAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
	
	@POST
	@Path("/bulk/sgdAnnotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateSgdAlleleDiseaseAnnotations(List<AlleleDiseaseAnnotationDTO> annotationData);
}
