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
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/construct")
@Tag(name = "CRUD - Constructs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstructCrudInterface extends BaseIdCrudInterface<Construct>, BaseDTOCrudControllerInterface<Construct, ConstructDTO> {

	@Override
	@GET
	@JsonView(View.ConstructView.class)
	@Path("/{id}")
	public ObjectResponse<Construct> get(@PathParam("id") Long id);
	
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.ConstructView.class)
	public ObjectResponse<Construct> get(@PathParam("identifier") String identifier);

	@PUT
	@Path("/")
	@JsonView(View.ConstructView.class)
	public ObjectResponse<Construct> update(Construct entity);

	@POST
	@Path("/")
	@JsonView(View.ConstructView.class)
	public ObjectResponse<Construct> create(Construct entity);

	@POST
	@Path("/bulk/{dataProvider}/constructs")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateConstructs(@PathParam("dataProvider") String dataProvider, List<ConstructDTO> constructData);

}
