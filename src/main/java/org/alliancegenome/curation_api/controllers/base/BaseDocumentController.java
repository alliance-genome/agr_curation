package org.alliancegenome.curation_api.controllers.base;

import java.util.HashMap;

import org.alliancegenome.curation_api.dao.base.BaseDocumentDAO;
import org.alliancegenome.curation_api.document.base.BaseDocument;
import org.alliancegenome.curation_api.interfaces.base.BaseSearchControllerInterface;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDocumentService;

public abstract class BaseDocumentController<S extends BaseDocumentService<E, D>, E extends BaseDocument, D extends BaseDocumentDAO<E>> implements BaseSearchControllerInterface<E> {

	private BaseDocumentService<E, D> service;

	protected void setService(S service) {
		this.service = service;
	}

	protected abstract void init();

	public SearchResponse<E> search(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null)
			params = new HashMap<String, Object>();
		Pagination pagination = new Pagination(page, limit);
		return service.searchByParams(pagination, params);
	}

}
