package org.alliancegenome.curation_api.base.services;

import java.util.Map;

import org.alliancegenome.curation_api.base.dao.*;
import org.alliancegenome.curation_api.base.document.BaseDocument;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseDocumentService<E extends BaseDocument, D extends BaseDocumentDAO<E>> {
	
	protected BaseESDAO<E> dao;
	
	protected void setESDao(BaseESDAO<E> dao) {
		this.dao = dao;
	}
	
	protected abstract void init();
	
	
	public ObjectResponse<E> get(String id) {
		E object = dao.find(id);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}
	
	public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
		return dao.searchByParams(pagination, params);
	}
}
