package org.alliancegenome.curation_api.base.controllers;

import java.util.*;

import javax.ws.rs.*;

import org.alliancegenome.curation_api.base.dao.BaseEntityDAO;
import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.base.interfaces.*;
import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;

public abstract class BaseCrudController<S extends BaseCrudService<E, D>, E extends BaseEntity, D extends BaseEntityDAO<E>> implements BaseIdCrudInterface<E>, BaseCurieCrudInterface<E> {

	private BaseCrudService<E, D> service;

	protected void setService(S service) {
		this.service = service;
	}

	protected abstract void init();

	public ObjectResponse<E> create(E entity) {
		return service.create(entity);
	}
	public ObjectListResponse<E> create(List<E> entities) {
		return service.create(entities);
	}

	public ObjectResponse<E> get(Long id) {
		return service.get(id);
	}
	public ObjectResponse<E> get(String curie) {
		return service.get(curie);
	}

	public ObjectResponse<E> update(E entity) {
		return service.update(entity);
	}

	public ObjectResponse<E> delete(Long id) {
		return service.delete(id);
	}
	public ObjectResponse<E> delete(String curie) {
		return service.delete(curie);
	}

	public SearchResponse<E> findByField(String field, String value) {
		return service.findByField(field, value);
	}

	public SearchResponse<E> find(Integer page, Integer limit, HashMap<String, Object> params) {
		if(params == null) params = new HashMap<>();
		Pagination pagination = new Pagination();
		pagination.setLimit(limit);
		pagination.setPage(page);
		return service.findByParams(pagination, params);
	}

	public SearchResponse<E> search(Integer page, Integer limit, HashMap<String, Object> params) {
		if(params == null) params = new HashMap<>();
		Pagination pagination = new Pagination();
		pagination.setLimit(limit);
		pagination.setPage(page);
		return service.searchByParams(pagination, params);
	}

	public void reindex(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		service.reindex(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

	public void reindexEverything(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		service.reindexEverything(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

}
