package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseDeleteIdControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadIdControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseUpdateControllerInterface;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseIdCrudInterface<E extends AuditedObject> extends
	BaseCreateControllerInterface<E>,
	BaseReadIdControllerInterface<E>,
	BaseUpdateControllerInterface<E>,
	BaseDeleteIdControllerInterface<E>,
	BaseSearchControllerInterface<E>,
	BaseFindControllerInterface<E>,
	BaseReindexControllerInterface {
	
}
