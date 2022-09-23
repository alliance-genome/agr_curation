package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/note")
@Tag(name = "CRUD - Notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NoteCrudInterface extends BaseIdCrudInterface<Note> {
	
	@POST
	@Path("/validate")
	@JsonView(View.NoteView.class)
	public ObjectResponse<Note> validate(Note entity);
	
	@Override
	@GET
	@JsonView(View.NoteView.class)
	@Path("/{id}")
	public ObjectResponse<Note> get(@PathParam("id") Long id);
	
	@POST
	@Path("/")
	@JsonView(View.NoteView.class)
	public ObjectResponse<Note> create(Note entity);
}
