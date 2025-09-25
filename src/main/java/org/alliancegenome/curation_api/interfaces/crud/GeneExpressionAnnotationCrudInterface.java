package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/gene-expression-annotation")
@Tag(name = "CRUD - Gene Expression Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionAnnotationCrudInterface extends BaseIdCrudInterface<GeneExpressionAnnotation> {

	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<GeneExpressionAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@GET
	@Path("/annotatedGeneList")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<Set<String>> geneExpressionAnnotationMap();

	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateExpressionAnnotations(@PathParam("dataProvider") String dataProvider, List<GeneExpressionFmsDTO> annotationData);
}
