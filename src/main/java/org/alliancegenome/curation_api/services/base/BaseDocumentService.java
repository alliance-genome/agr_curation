package org.alliancegenome.curation_api.services.base;

import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseDocumentDAO;
import org.alliancegenome.curation_api.dao.base.BaseESDAO;
import org.alliancegenome.curation_api.document.base.BaseDocument;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;

public abstract class BaseDocumentService<E extends BaseDocument, D extends BaseDocumentDAO<E>> {

	protected BaseESDAO<E> dao;

	protected void setESDao(BaseESDAO<E> dao) {
		this.dao = dao;
	}

	protected abstract void init();

	public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
		return dao.searchByParams(pagination, params);
	}
}
