package org.alliancegenome.curation_api.dao.base;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.reflections.Reflections;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;

@ApplicationScoped
public class SystemSQLDAO extends BaseSQLDAO<AuditedObject> {

	@Inject
	LiteratureReferenceDAO literatureReferenceDAO;

	protected SystemSQLDAO() {
		super(AuditedObject.class);
	}

	public ObjectResponse<Map<String, Object>> getSiteSummary() {

		Reflections reflections = new Reflections("org.alliancegenome.curation_api");
		Set<Class<?>> entityClasses = reflections.get(TypesAnnotated.with(Entity.class).asClass(reflections.getConfiguration().getClassLoaders()));
		Set<Class<?>> indexedClasses = reflections.get(TypesAnnotated.with(Indexed.class).asClass(reflections.getConfiguration().getClassLoaders()));

		Set<Class<?>> allClasses = new HashSet<Class<?>>();

		allClasses.addAll(entityClasses);
		allClasses.addAll(indexedClasses);

		Map<String, Object> map = new HashMap<>();

		for (Class<?> clazz : allClasses) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			if (entityClasses.contains(clazz)) {
				tempMap.put("dbCount", dbCount(clazz));
			} else {
				tempMap.put("dbCount", 0);
			}
			if (indexedClasses.contains(clazz) || clazz.getSimpleName().equals("Reference")) {
				if (clazz.getSimpleName().equals("Reference")) {
					SearchResponse<LiteratureReference> res = literatureReferenceDAO.searchAllCount();
					tempMap.put("esCount", res.getTotalResults());
				} else {
					tempMap.put("esCount", esCount(clazz));
				}
			} else {
				tempMap.put("esCount", 0);
			}
			map.put(clazz.getSimpleName(), tempMap);

		}

		return new ObjectResponse<Map<String, Object>>(map);
	}

}
