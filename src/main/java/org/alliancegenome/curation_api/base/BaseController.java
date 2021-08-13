package org.alliancegenome.curation_api.base;

import java.util.*;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.dto.Pagination;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public abstract class BaseController<S extends BaseService<E, D>, E extends BaseEntity, D extends BaseSQLDAO<E>> {

	private BaseService<E, D> service;

	protected void setService(S service) {
		this.service = service;
	}
	
	protected abstract void init();
	
	public E create(E entity) {
		return service.create(entity);
	}
	
	public E get(String id) {
		return service.get(id);
	}

	public E update(E entity) {
		return service.update(entity);
	}

	public E delete(String id) {
		return service.delete(id);
	}

	public SearchResults<E> getAll(Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		pagination.setLimit(limit);
		pagination.setPage(page);
		return service.getAll(pagination);
	}

	public List<E> find(HashMap<String, Object> params) {
		return service.findByParams(params);
	}

	public SearchResults<E> search(Integer page, Integer limit, HashMap<String, Object> params) {
		if(params == null) params = new HashMap<String, Object>();
		Pagination pagination = new Pagination();
		pagination.setLimit(limit);
		pagination.setPage(page);
		return service.searchByParams(pagination, params);
	}
	
	public void reindex() {
		service.reindex();
	}
	
}
