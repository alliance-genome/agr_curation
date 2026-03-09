package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/note")
@Tag(name = "CRUD - Notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NoteCrudInterface extends BaseIdCrudInterface<Note> {

	@Operation(summary = "Validate note", description = "Validate a note entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.NoteView.class)
	ObjectResponse<Note> validate(Note entity);

	@Override
	@GET
	@JsonView(CurationView.NoteView.class)
	@Path("/{id}")
	ObjectResponse<Note> getById(@PathParam("id") Long id);

	@Operation(summary = "Create note", description = "Create a new note entity")
	@POST
	@Path("/")
	@JsonView(CurationView.NoteView.class)
	ObjectResponse<Note> create(Note entity);
}
