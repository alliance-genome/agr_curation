package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.response.SearchResponse;

import io.quarkus.logging.Log;

public class CurieObjectDAO<E extends CurieObject> extends BaseSQLDAO<E> {


	protected CurieObjectDAO(Class<E> myClass) {
		super(myClass);
	}

	public E findByCurie(String curie) {
		Log.debug("SqlDAO: findByCurie: " + curie + " " + myClass);
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
