package org.alliancegenome.curation_api.base.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.entity.BaseEntity;

@ApplicationScoped
public class SystemSQLDAO extends BaseSQLDAO<BaseEntity> {

    protected SystemSQLDAO() {
        super(BaseEntity.class);
    }

}
