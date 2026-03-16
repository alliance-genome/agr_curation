package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
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
}