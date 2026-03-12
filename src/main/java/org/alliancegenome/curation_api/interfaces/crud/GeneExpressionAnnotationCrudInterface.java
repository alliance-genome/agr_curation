package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import java.util.HashMap;
import org.alliancegenome.curation_api.response.SearchResponse;

@Path("/gene-expression-annotation")
@Tag(name = "CRUD - Gene Expression Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionAnnotationCrudInterface extends BaseIdCrudInterface<GeneExpressionAnnotation> {

	@Operation(summary = "Get gene expression annotation by identifier", description = "Retrieve a single gene expression annotation by its identifier")
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<GeneExpressionAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@Operation(summary = "Get annotated gene identifiers", description = "Retrieve list of gene identifiers that have gene expression annotation annotations")
	@GET
	@Path("/annotatedGeneList")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectListResponse<String> annotatedGeneList();

	@Operation(summary = "Bulk load gene expression annotation data", description = "Bulk load gene expression annotation records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateExpressionAnnotations(@PathParam("dataProvider") String dataProvider, List<GeneExpressionFmsDTO> annotationData);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.GeneExpressionAnnotationView.class)
	SearchResponse<GeneExpressionAnnotation> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView(CurationView.GeneExpressionAnnotationView.class)
	SearchResponse<GeneExpressionAnnotation> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);
}
