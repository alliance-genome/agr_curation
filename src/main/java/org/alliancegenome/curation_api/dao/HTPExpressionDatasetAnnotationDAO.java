package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class HTPExpressionDatasetAnnotationDAO extends BaseSQLDAO<HTPExpressionDatasetAnnotation> {

	protected HTPExpressionDatasetAnnotationDAO() {
		super(HTPExpressionDatasetAnnotation.class);
	}

	public List<Long> getAllHTPDatasetSearchResultIds() {
		String sql = """
				SELECT id
				FROM htpexpressiondatasetannotation
				WHERE obsolete = false
				AND internal = false
				ORDER BY id
				""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<HTPExpressionDatasetAnnotation> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String jpql = """
				SELECT DISTINCT h FROM HTPExpressionDatasetAnnotation h
				LEFT JOIN FETCH h.htpExpressionDataset
				LEFT JOIN FETCH h.dataProvider
				LEFT JOIN FETCH h.relatedNote
				LEFT JOIN FETCH h.categoryTags
				WHERE h.id IN :ids
				""";
		return entityManager.createQuery(jpql, HTPExpressionDatasetAnnotation.class)
			.setParameter("ids", ids)
			.getResultList();
	}
}
