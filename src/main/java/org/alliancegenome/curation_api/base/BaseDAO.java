package org.alliancegenome.curation_api.base;

public abstract class BaseDAO<E extends BaseEntity> {

    protected Class<E> myClass;

    protected BaseDAO(Class<E> myClass) {
        this.myClass = myClass;
    }

}
