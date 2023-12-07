package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.transaction.Transactional;

public abstract class SubmittedObjectCrudService<E extends SubmittedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends CurieObjectCrudService<E, D> {

	@Override
	public ObjectResponse<E> get(String identifierString) {
		E object = dao.findByIdentifierString(identifierString);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Override
	@Transactional
	public ObjectResponse<E> delete(String identifierString) {
		E object = dao.findByIdentifierString(identifierString);
		if (object != null)
			dao.remove(object.getId());
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}
	
	public abstract E upsert(T dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException;

	public abstract void removeOrDeprecateNonUpdated(Long id, String loadDescription);

}
