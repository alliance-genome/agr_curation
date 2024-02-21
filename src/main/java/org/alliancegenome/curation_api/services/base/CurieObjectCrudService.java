package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;

import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;

public abstract class CurieObjectCrudService<E extends CurieObject, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	public ObjectResponse<E> get(String curie) {
		E object = findByCurie(curie);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> delete(String curie) {
		E object = findByCurie(curie);
		if (object != null)
			dao.remove(object.getId());
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}
	
	public E findByCurie(String curie) {
		if (curie != null) {
			SearchResponse<E> response = findByField("curie", curie);
			if (response == null || response.getSingleResult() == null) {
				Log.debug("Entity Not Found: " + curie);
				return null;
			}
			E entity = response.getSingleResult();
			Log.debug("Entity Found: " + entity);
			return entity;
		} else {
			Log.debug("Input Param is null: " + curie);
			return null;
		}
	}

}
