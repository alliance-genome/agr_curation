package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.transaction.Transactional;

public abstract class CurieObjectCrudService<E extends CurieObject, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	public ObjectResponse<E> get(String curie) {
		E object = dao.findByCurie(curie);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> delete(String curie) {
		E object = dao.findByCurie(curie);
		if (object != null)
			dao.remove(object.getId());
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

}
