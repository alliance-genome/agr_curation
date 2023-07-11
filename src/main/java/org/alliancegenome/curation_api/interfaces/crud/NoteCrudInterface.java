package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Note;
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
