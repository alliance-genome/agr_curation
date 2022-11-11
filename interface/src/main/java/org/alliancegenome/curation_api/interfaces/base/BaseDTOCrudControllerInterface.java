package org.alliancegenome.curation_api.interfaces.base;

import javax.ws.rs.*;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.base.BaseEntity;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

public interface BaseDTOCrudControllerInterface<E extends BaseEntity, T extends BaseDTO> {

	@POST
	@Path("/upsert")
	@JsonView(View.FieldsOnly.class)
	public E upsert(T dto) throws ObjectUpdateException;
}
