package org.alliancegenome.curation_api.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneDiseaseAnnotationDAO extends BaseSQLDAO<GeneDiseaseAnnotation> {

	protected GeneDiseaseAnnotationDAO() {
		super(GeneDiseaseAnnotation.class);
	}

	public Set<String> getGeneDiseaseAnnotationMap() {
		String hql = """
				select distinct diseaseAnnotationSubject.primaryExternalId
				from GeneDiseaseAnnotation
				where obsolete = false and internal = false
				""";
		Query query = entityManager.createQuery(hql);
		List<Object> list = query.getResultList();
		return new HashSet<>(list.stream().map(o -> (String) o).toList());
	}
}
