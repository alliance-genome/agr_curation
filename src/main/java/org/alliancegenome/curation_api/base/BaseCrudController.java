package org.alliancegenome.curation_api.base;

import java.util.HashMap;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public abstract class BaseCrudController<S extends BaseService<E, D>, E extends BaseEntity, D extends BaseDAO<E>> implements BaseIdCrudInterface<E> {

    private BaseService<E, D> service;

    protected void setService(S service) {
        this.service = service;
    }
    
    protected abstract void init();
    
    public ObjectResponse<E> create(E entity) {
        return service.create(entity);
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
        if(params == null) params = new HashMap<String, Object>();
        Pagination pagination = new Pagination();
        pagination.setLimit(limit);
        pagination.setPage(page);
        return service.findByParams(pagination, params);
    }

    public SearchResponse<E> search(Integer page, Integer limit, HashMap<String, Object> params) {
        if(params == null) params = new HashMap<String, Object>();
        Pagination pagination = new Pagination();
        pagination.setLimit(limit);
        pagination.setPage(page);
        return service.searchByParams(pagination, params);
    }
    
    public void reindex(Integer threads, Integer indexAmount) {
        service.reindex(threads, indexAmount);
    }
}
