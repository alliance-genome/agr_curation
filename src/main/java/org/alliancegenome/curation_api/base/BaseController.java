package org.alliancegenome.curation_api.base;

import java.util.*;

import javax.enterprise.context.RequestScoped;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public abstract class BaseController<S extends BaseService<E, D>, E extends BaseEntity, D extends BaseSQLDAO<E>> {

	private BaseService<E, D> service;

	protected void setService(S service) {
		log.debug("Does this get called? " + service);
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

	public List<E> getAll() {
		return service.getAll();
	}

	public List<E> find(Map<String, Object> params) {
		return service.findByParams(params);
	}

//	public SearchResult<E> search(Map<String, Object> params) {
//		return service.searchByParams(params);
//	}
	
	public void reindex() {
		service.reindex();
	}
	
}
