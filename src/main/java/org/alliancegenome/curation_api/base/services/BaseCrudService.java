package org.alliancegenome.curation_api.base.services;

import java.util.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.base.dao.*;
import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseCrudService<E extends BaseEntity, D extends BaseEntityDAO<E>> {
    
    protected BaseSQLDAO<E> dao;

    protected void setSQLDao(BaseSQLDAO<E> dao) {
        this.dao = dao;
    }
    
    @Inject
    @AuthenticatedUser
    protected LoggedInPerson authenticatedPerson;
    
    protected abstract void init();
    
    @Transactional
    public ObjectResponse<E> create(E entity) {
        //log.info("Authed Person: " + authenticatedPerson);
        E object = dao.persist(entity);
        ObjectResponse<E> ret = new ObjectResponse<E>(object);
        return ret;
    }
    
    @Transactional
    public ObjectListResponse<E> create(List<E> entities) {
        //log.info("Authed Person: " + authenticatedPerson);
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
        //log.info("Authed Person: " + authenticatedPerson);
        E object = dao.merge(entity);
        ObjectResponse<E> ret = new ObjectResponse<E>(object);
        return ret;
    }

    @Transactional
    public ObjectResponse<E> delete(String id) {
        //log.info("Authed Person: " + authenticatedPerson);
        E object = dao.remove(id);
        ObjectResponse<E> ret = new ObjectResponse<E>(object);
        return ret;
    }

    @Transactional
    public ObjectResponse<E> delete(Long id) {
        E object = dao.remove(id);
        ObjectResponse<E> ret = new ObjectResponse<E>(object);
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

    public void reindex(int threads, int indexAmount, int batchSize) {
        dao.reindex(threads, indexAmount, batchSize);
    }

}
