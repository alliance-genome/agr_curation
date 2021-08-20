package org.alliancegenome.curation_api.base;

import java.util.*;

import javax.transaction.Transactional;

import org.alliancegenome.curation_api.model.input.Pagination;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseService<E extends BaseEntity, D extends BaseSQLDAO<E>> {
    
    private BaseSQLDAO<E> dao;

    protected void setSQLDao(D dao) {
        this.dao = dao;
    }
    
    protected abstract void init();
    
    @Transactional
    public E create(E entity) {
        return dao.persist(entity);
    }

    public E get(String id) {
        return dao.find(id);
    }

    @Transactional
    public E update(E entity) {
        return dao.merge(entity);
    }

    @Transactional
    public E delete(String id) {
        return dao.remove(id);
    }

    public SearchResults<E> getAll(Pagination pagination) {
        return dao.findAll(pagination);
    }
    
    public List<E> findByParams(Map<String, Object> params) {
        return dao.findByParams(params);
    }
    
    public SearchResults<E> searchByParams(Pagination pagination, Map<String, Object> params) {
        return dao.searchByParams(pagination, params);
    }

    public void reindex() {
        dao.reindex();
    }
}
