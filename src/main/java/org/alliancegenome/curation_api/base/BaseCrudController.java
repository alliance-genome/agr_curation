package org.alliancegenome.curation_api.base;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.response.ObjectResponse;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public abstract class BaseCrudController<S extends BaseService<E, D>, E extends BaseEntity, D extends BaseDAO<E>> extends BaseSearchController<S, E, D> implements BaseCrudInterface<E> {

    private BaseService<E, D> service;

    protected void setService(S service) {
        super.setService(service);
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
    
}
