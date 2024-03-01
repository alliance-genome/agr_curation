package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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

@Path("/phenotype-annotation")
@Tag(name = "CRUD - Phenotype Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PhenotypeAnnotationCrudInterface extends BaseIdCrudInterface<PhenotypeAnnotation> {

	@GET
	@Path("/findBy/{modEntityId|modInternalId|uniqueId}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<PhenotypeAnnotation> get(@PathParam("identifier") String identifier);

	@Override
	@POST
	@Path("/search")
	@JsonView(View.PhenotypeAnnotationView.class)
	@Tag(name = "Elastic Search Phenotype Annotations")
	public SearchResponse<PhenotypeAnnotation> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@POST
	@Path("/bulk/{dataProvider}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updatePhenotypeAnnotations(@PathParam("dataProvider") String dataProvider, List<PhenotypeFmsDTO> annotationData);

}
