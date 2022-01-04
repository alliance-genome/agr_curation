package org.alliancegenome.curation_api.base.dao;

import org.alliancegenome.curation_api.base.entity.BaseEntity;

public abstract class BaseDAO<E extends BaseEntity> {

    protected Class<E> myClass;

    protected BaseDAO(Class<E> myClass) {
        this.myClass = myClass;
    }

}
