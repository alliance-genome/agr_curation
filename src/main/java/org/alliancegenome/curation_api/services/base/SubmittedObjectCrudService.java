package org.alliancegenome.curation_api.services.base;

import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseDeleteIdentifierControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadIdControllerInterface;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.transaction.Transactional;

public abstract class SubmittedObjectCrudService<E extends SubmittedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> implements 
	BaseReadIdControllerInterface<E>,
	BaseDeleteIdentifierControllerInterface<E>,
	BaseUpsertControllerInterface<E, T>
{

	public ObjectResponse<E> getByIdentifier(String identifierString) {
		E object = findByIdentifierString(identifierString);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
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
	
	public E upsert(T dto) throws ObjectUpdateException {
		return upsert(dto, null);
	}
	public abstract E upsert(T dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException;

	public abstract void removeOrDeprecateNonUpdated(Long id, String loadDescription);

	public E findByIdentifierString(String id) {
		if (id != null && id.startsWith("AGRKB:"))
			return findByCurie(id);
		
		List<String> alternativeFields = List.of("modEntityId", "modInternalId");
		return findByAlternativeFields(alternativeFields, id);
	}
}
