package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.response.SearchResponse;

import io.quarkus.logging.Log;

public class SubmittedObjectDAO<E extends SubmittedObject> extends CurieObjectDAO<E> {


	protected SubmittedObjectDAO(Class<E> myClass) {
		super(myClass);
	}

	public E findByIdentifierString(String id) {
		Log.debug("SqlDAO: findByIdentifierString: " + id + " " + myClass);
		if (id != null) {
			SearchResponse<E> response = null;
			if (id.startsWith("AGRKB:")) {
				response = findByField("curie", id);
			} else {
				if (response == null || response.getSingleResult() == null) {
					response = findByField("modEntityId", id);
					if (response == null || response.getSingleResult() == null)
						response = findByField("modInternalId", id);
				}
			}
			
			if (response == null || response.getSingleResult() == null) {
				Log.debug("Entity Not Found: " + id);
				return null;
			}
			
			E entity = response.getSingleResult();
			Log.debug("Entity Found: " + entity);
			return entity;
		} else {
			Log.debug("Input Param is null: " + id);
			return null;
		}
	}

}
