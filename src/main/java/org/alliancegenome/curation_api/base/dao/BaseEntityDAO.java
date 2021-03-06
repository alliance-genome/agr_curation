package org.alliancegenome.curation_api.base.dao;

import org.alliancegenome.curation_api.base.entity.BaseEntity;

public abstract class BaseEntityDAO<E extends BaseEntity> {

	protected Class<E> myClass;

	protected BaseEntityDAO(Class<E> myClass) {
		this.myClass = myClass;
	}

}
