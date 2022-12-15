package org.alliancegenome.curation_api.dao.base;

import org.alliancegenome.curation_api.document.base.BaseDocument;

public abstract class BaseDocumentDAO<E extends BaseDocument> {

	protected Class<E> myClass;
	protected String esIndex;

	protected BaseDocumentDAO(Class<E> myClass, String esIndex) {
		this.myClass = myClass;
		this.esIndex = esIndex;
	}
}
