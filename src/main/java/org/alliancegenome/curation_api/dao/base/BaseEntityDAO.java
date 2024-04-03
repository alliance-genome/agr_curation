package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;

public abstract class BaseEntityDAO<E extends AuditedObject> {

	protected Class<E> myClass;

	protected BaseEntityDAO(Class<E> myClass) {
		this.myClass = myClass;
	}

}
