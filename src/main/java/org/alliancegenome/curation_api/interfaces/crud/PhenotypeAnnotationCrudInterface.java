package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
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

@Path("/phenotype-annotation")
@Tag(name = "CRUD - Phenotype Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PhenotypeAnnotationCrudInterface extends BaseIdCrudInterface<PhenotypeAnnotation> {

	@Operation(summary = "Get phenotype annotation by identifier", description = "Retrieve a single phenotype annotation by its identifier")
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<PhenotypeAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@Override
	@GET
	@Path("/{id}")
	@JsonView(CurationView.FieldsAndLists.class)
	ObjectResponse<PhenotypeAnnotation> getById(@PathParam("id") Long id);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView(CurationView.PhenotypeAnnotationView.class)
	SearchResponse<PhenotypeAnnotation> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Operation(summary = "Bulk load phenotype annotation data", description = "Bulk load phenotype annotation records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updatePhenotypeAnnotations(@PathParam("dataProvider") String dataProvider, List<PhenotypeFmsDTO> annotationData);

}
