package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class AGMDiseaseAnnotationDAO extends BaseSQLDAO<AGMDiseaseAnnotation> {

	protected AGMDiseaseAnnotationDAO() {
		super(AGMDiseaseAnnotation.class);
	}

	public List<String> getAgmDiseaseAnnotationList() {
		String hql = """
				select distinct inferredGene.primaryExternalId
				from AGMDiseaseAnnotation
				where obsolete = false and internal = false
				""";
		Query query = entityManager.createQuery(hql);
		List<Object> list = query.getResultList();

		return new ArrayList<>(list.stream().map(o -> (String) o).toList());
	}
}
