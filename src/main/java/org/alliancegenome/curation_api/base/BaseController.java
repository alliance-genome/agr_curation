package org.alliancegenome.curation_api.base;

import java.util.HashMap;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public abstract class BaseController<S extends BaseService<E, D>, E extends BaseEntity, D extends BaseDAO<E>> implements BaseCrudRESTInterface<E> {

    private BaseService<E, D> service;

    protected void setService(S service) {
        this.service = service;
    }
    
    protected abstract void init();
    
    public ObjectResponse<E> create(E entity) {
        return service.create(entity);
    }

    public ObjectResponse<E> get(String id) {
        return service.get(id);
    }

    public ObjectResponse<E> update(E entity) {
        return service.update(entity);
    }

    public ObjectResponse<E> delete(String id) {
        return service.delete(id);
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
    
    public void reindex() {
        service.reindex();
    }
    
}
