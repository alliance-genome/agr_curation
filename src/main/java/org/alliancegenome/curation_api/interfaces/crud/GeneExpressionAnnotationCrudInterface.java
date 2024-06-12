package org.alliancegenome.curation_api.interfaces.crud;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;

@Path("/expression-annotation")
@Tag(name = "CRUD - Expression Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionAnnotationCrudInterface extends BaseIdCrudInterface<GeneExpressionAnnotation> {

	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<GeneExpressionAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@Override
	@POST
	@Path("/search")
	@JsonView(View.FieldsAndLists.class)
	@Tag(name = "Elastic Search Gene Expression Annotations")
	SearchResponse<GeneExpressionAnnotation> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateExpressionAnnotations(@PathParam("dataProvider") String dataProvider, List<GeneExpressionFmsDTO> annotationData);


}
