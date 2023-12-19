package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;

public class SubmittedObjectDAO<E extends SubmittedObject> extends CurieObjectDAO<E> {


	protected SubmittedObjectDAO(Class<E> myClass) {
		super(myClass);
	}

}
