package org.alliancegenome.curation_api.services.base;

import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.transaction.Transactional;

public abstract class SubmittedObjectCrudService<E extends SubmittedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> implements BaseUpsertServiceInterface<E, T> {

	public ObjectResponse<E> getByIdentifier(String identifier) {
		E object = findByIdentifierString(identifier);
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> deleteByIdentifier(String identifierString) {
		E object = findByIdentifierString(identifierString);
		if (object != null)
			dao.remove(object.getId());
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

	public abstract void removeOrDeprecateNonUpdated(Long id, String loadDescription);

	public E findByIdentifierString(String id) {
		if (id != null && id.startsWith("AGRKB:"))
			return findByCurie(id);
		
		return findByAlternativeFields(List.of("modEntityId", "modInternalId"), id);
	}
}
