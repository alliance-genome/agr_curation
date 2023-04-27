package org.alliancegenome.curation_api.services.base;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.BaseEntity;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.transaction.Transactional;

public abstract class BaseEntityCrudService<E extends BaseEntity, D extends BaseEntityDAO<E>> {

	protected BaseSQLDAO<E> dao;

	protected void setSQLDao(BaseSQLDAO<E> dao) {
		this.dao = dao;
	}

	@Inject
	@AuthenticatedUser
	protected Person authenticatedPerson;

	protected abstract void init();

	@Transactional
	public ObjectResponse<E> create(E entity) {
		// log.info("Authed Person: " + authenticatedPerson);
		E object = dao.persist(entity);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectListResponse<E> create(List<E> entities) {
		// log.info("Authed Person: " + authenticatedPerson);
		List<E> objects = dao.persist(entities);
		ObjectListResponse<E> ret = new ObjectListResponse<E>(objects);
		return ret;
	}

	public ObjectResponse<E> get(Long id) {
		E object = dao.find(id);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	public ObjectResponse<E> get(String id) {
		E object = dao.find(id);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> update(E entity) {
		// log.info("Authed Person: " + authenticatedPerson);
		E object = dao.merge(entity);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> delete(String id) {
		// log.info("Authed Person: " + authenticatedPerson);
		E object = dao.remove(id);
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> delete(Long id) {
		E object = dao.remove(id);
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

	public SearchResponse<E> findByField(String field, String value) {
		return dao.findByField(field, value);
	}

	public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params) {
		return dao.findByParams(pagination, params);
	}

	public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
		return dao.searchByParams(pagination, params);
	}

	public void reindex() {
		dao.reindex();
	}

	public void reindex(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		dao.reindex(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

	public void reindexEverything(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout,
		Integer typesToIndexInParallel) {
		dao.reindexEverything(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

}
