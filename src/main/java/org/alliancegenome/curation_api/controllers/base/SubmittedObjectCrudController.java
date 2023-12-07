package org.alliancegenome.curation_api.controllers.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.base.SubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;

public abstract class SubmittedObjectCrudController<S extends SubmittedObjectCrudService<E, T, D>, E extends SubmittedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends CurieObjectCrudController<S, E, D> implements SubmittedObjectCrudInterface<E> {

	protected SubmittedObjectCrudService<E, T, D> service;
	
	@Override
	public ObjectResponse<E> get(String identifierString) {
		return service.get(identifierString);
	}

	@Override
	public ObjectResponse<E> delete(String identifierString) {
		return service.delete(identifierString);
	}
	
	public E upsert(T dto) throws ObjectUpdateException {
		return upsert(dto, null);
	}

	public E upsert(T dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return service.upsert(dto, dataProvider);
	}

}
