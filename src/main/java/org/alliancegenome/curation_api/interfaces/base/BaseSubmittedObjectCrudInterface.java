package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseDeleteIdentifierControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadIdentifierControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseUpdateControllerInterface;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseSubmittedObjectCrudInterface<E extends SubmittedObject> extends 
	BaseCreateControllerInterface<E>,
	BaseReadIdentifierControllerInterface<E>,
	BaseUpdateControllerInterface<E>,
	BaseDeleteIdentifierControllerInterface<E>,
	BaseSearchControllerInterface<E>,
	BaseFindControllerInterface<E>,
	BaseReindexControllerInterface {

}
