package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.model.entities.base.BaseEntity;

public abstract class BaseEntityDAO<E extends BaseEntity> {

	protected Class<E> myClass;

	protected BaseEntityDAO(Class<E> myClass) {
		this.myClass = myClass;
	}

}
