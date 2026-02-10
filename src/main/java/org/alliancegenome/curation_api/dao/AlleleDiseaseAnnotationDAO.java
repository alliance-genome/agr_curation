package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class AlleleDiseaseAnnotationDAO extends BaseSQLDAO<AlleleDiseaseAnnotation> {

	protected AlleleDiseaseAnnotationDAO() {
		super(AlleleDiseaseAnnotation.class);
	}

	public List<String> getGeneDiseaseList() {
		String hql = """
				select distinct inferredGene.primaryExternalId
				from AlleleDiseaseAnnotation
				where obsolete = false and internal = false
				""";
		Query query = entityManager.createQuery(hql);
		List<Object> list = query.getResultList();
		return new ArrayList<>(list.stream().map(o -> (String) o).toList());
	}
}
