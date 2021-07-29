package org.alliancegenome.curation_api.base;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BaseDAO<E extends BaseEntity> {

	protected Class<E> myClass;

	protected BaseDAO(Class<E> myClass) {
		this.myClass = myClass;
	}

}
