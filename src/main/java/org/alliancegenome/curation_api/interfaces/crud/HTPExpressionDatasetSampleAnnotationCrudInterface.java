package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.HTPExpressionDatasetSampleAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/htpexpressiondatasetsampleannotation")
@Tag(name = "CRUD - HTP Expression Dataset Sample Annotation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface HTPExpressionDatasetSampleAnnotationCrudInterface extends BaseIdCrudInterface<HTPExpressionDatasetSampleAnnotation> {

	@Operation(summary = "Bulk load HTP expression dataset sample annotation data", description = "Bulk load HTP expression dataset sample annotation records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/htpexpressiondatasetsampleannotationfile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateHTPExpressionDatasetSampleAnnotation(@PathParam("dataProvider") String dataProvider, HTPExpressionDatasetSampleAnnotationIngestFmsDTO htpDatasetSampleData);

	// SCRUM-6715: direct LinkML DQM submission, alongside the FMS endpoint above. Mirrors
	// HTPExpressionDatasetAnnotationCrudInterface's own bulk/{dataProvider}/htpexpressiondatasetannotations endpoint.
	@Operation(summary = "Bulk load HTP expression dataset sample annotation data via direct LinkML submission", description = "Bulk load HTP expression dataset sample annotation records submitted directly as LinkML DTOs")
	@POST
	@Path("/bulk/{dataProvider}/htpexpressiondatasetsampleannotations")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateHTPExpressionDatasetSampleAnnotations(@PathParam("dataProvider") String dataProvider, List<HTPExpressionDatasetSampleAnnotationDTO> htpDatasetSampleData);

	// SCRUM-6534: the detail page's stable URL. BaseIdCrudInterface's getById is keyed on the numeric
	// DB id and @JsonView(FieldsOnly), which would omit htpExpressionSampleLocations/relatedNotes/
	// datasetIds (FieldsAndLists only) - mirrors HTPExpressionDatasetAnnotationCrudInterface's own
	// findBy/{identifier}.
	@Operation(summary = "Get HTP expression dataset sample annotation by identifier", description = "Retrieve a single HTP expression dataset sample annotation by its curie")
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<HTPExpressionDatasetSampleAnnotation> getByIdentifier(@PathParam("identifier") String identifier);
}