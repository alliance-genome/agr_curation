package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
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

@Path("/disease-annotation")
@Tag(name = "CRUD - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationCrudInterface extends BaseIdCrudInterface<DiseaseAnnotation> {

	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<DiseaseAnnotation> getByIdentifier(@PathParam("identifier") String identifier);

	@Override
	@POST
	@Path("/search")
	@JsonView(View.DiseaseAnnotation.class)
	@Tag(name = "Elastic Search Disease Annotations")
	SearchResponse<DiseaseAnnotation> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
