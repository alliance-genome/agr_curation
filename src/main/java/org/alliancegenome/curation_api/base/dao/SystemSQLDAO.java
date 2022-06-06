package org.alliancegenome.curation_api.base.dao;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.reflections.Reflections;

@ApplicationScoped
public class SystemSQLDAO extends BaseSQLDAO<BaseEntity> {

    protected SystemSQLDAO() {
        super(BaseEntity.class);
    }
    
    public ObjectResponse<Map<String, Object>> getSiteSummary() {
        
        Reflections reflections = new Reflections("org.alliancegenome.curation_api");
        Set<Class<?>> annotatedClasses = reflections.get(TypesAnnotated.with(Entity.class).asClass(reflections.getConfiguration().getClassLoaders()));
        
        Map<String, Object> map = new HashMap<>();
        
        for(Class<?> clazz: annotatedClasses) {
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("dbCount", dbCount(clazz));
            tempMap.put("esCount", esCount(clazz));
            map.put(clazz.getSimpleName(), tempMap);
        }
        
        return new ObjectResponse<Map<String, Object>>(map);
    }

}
