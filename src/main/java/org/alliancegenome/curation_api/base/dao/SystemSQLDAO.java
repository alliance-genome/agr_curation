package org.alliancegenome.curation_api.base.dao;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.reflections.Reflections;

@ApplicationScoped
public class SystemSQLDAO extends BaseSQLDAO<BaseEntity> {

	protected SystemSQLDAO() {
		super(BaseEntity.class);
	}
	
	public ObjectResponse<Map<String, Object>> getSiteSummary() {
		
		Reflections reflections = new Reflections("org.alliancegenome.curation_api");
		Set<Class<?>> entityClasses = reflections.get(TypesAnnotated.with(Entity.class).asClass(reflections.getConfiguration().getClassLoaders()));
		Set<Class<?>> indexedClasses = reflections.get(TypesAnnotated.with(Indexed.class).asClass(reflections.getConfiguration().getClassLoaders()));
		
		Set<Class<?>> allClasses = new HashSet<Class<?>>();
		
		allClasses.addAll(entityClasses);
		allClasses.addAll(indexedClasses);
		
		Map<String, Object> map = new HashMap<>();

		for(Class<?> clazz: allClasses) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			if(entityClasses.contains(clazz)) {
				tempMap.put("dbCount", dbCount(clazz));
			} else {
				tempMap.put("dbCount", 0);
			}
			if(indexedClasses.contains(clazz)) {
				tempMap.put("esCount", esCount(clazz));
			} else {
				tempMap.put("esCount", 0);
			}
			map.put(clazz.getSimpleName(), tempMap);
		}
		
		return new ObjectResponse<Map<String, Object>>(map);
	}

}
