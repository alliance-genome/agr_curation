package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseDeleteCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadCurieControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseUpdateControllerInterface;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseCurieObjectCrudInterface<E extends CurieObject> extends 
	BaseCreateControllerInterface<E>,
	BaseReadCurieControllerInterface<E>,
	BaseUpdateControllerInterface<E>,
	BaseDeleteCurieControllerInterface<E>,
	BaseSearchControllerInterface<E>,
	BaseFindControllerInterface<E>,
	BaseReindexControllerInterface {

}
