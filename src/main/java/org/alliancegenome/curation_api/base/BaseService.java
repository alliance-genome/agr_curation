package org.alliancegenome.curation_api.base;

import java.util.Map;

import javax.transaction.Transactional;

import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseService<E extends BaseEntity, D extends BaseDAO<E>> {
    
    protected BaseSQLDAO<E> dao;

    protected void setSQLDao(BaseSQLDAO<E> dao) {
        this.dao = dao;
    }
    
    protected abstract void init();
    
    @Transactional
    public ObjectResponse<E> create(E entity) {
        E object = dao.persist(entity);
        ObjectResponse<E> ret = new ObjectResponse<E>(object);
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
        E object = dao.merge(entity);
        ObjectResponse<E> ret = new ObjectResponse<E>(object);
        return ret;
    }

    @Transactional
    public ObjectResponse<E> delete(String id) {
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
    public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params) {
        return dao.findByParams(pagination, params);
    }
    
    public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
        return dao.searchByParams(pagination, params);
    }

    public void reindex() {
        dao.reindex();
    }

    public void reindex(int threads, int indexAmount) {
        dao.reindex(threads, indexAmount);
    }

}
