package org.alliancegenome.curation_api.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneExpressionAnnotationDAO extends BaseSQLDAO<GeneExpressionAnnotation> {

	protected GeneExpressionAnnotationDAO() {
		super(GeneExpressionAnnotation.class);
	}

	public Set<String> getGeneExpressionMap() {
		String hql = """
				select distinct expressionAnnotationSubject.primaryExternalId
				from GeneExpressionAnnotation
				where obsolete = false and internal = false
				""";
		Query query = entityManager.createQuery(hql);
		List<Object> list = query.getResultList();
		return new HashSet<>(list.stream().map(o -> (String)o).toList());
	}
}
