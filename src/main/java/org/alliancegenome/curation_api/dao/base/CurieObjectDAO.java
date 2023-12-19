package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.model.entities.base.CurieObject;

public class CurieObjectDAO<E extends CurieObject> extends BaseSQLDAO<E> {


	protected CurieObjectDAO(Class<E> myClass) {
		super(myClass);
	}

}
