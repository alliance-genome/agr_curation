package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubmittedObjectCrudInterface<E extends SubmittedObject> extends 
	BaseIdCrudInterface<E>,
	BaseSearchControllerInterface<E>,
	BaseReadIdentifierCurieControllerInterface<E>,
	BaseDeleteIdentifierControllerInterface<E>
{

}
