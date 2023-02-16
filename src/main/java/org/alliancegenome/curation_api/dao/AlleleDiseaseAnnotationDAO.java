package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;

@ApplicationScoped
public class AlleleDiseaseAnnotationDAO extends BaseSQLDAO<AlleleDiseaseAnnotation> {

	protected AlleleDiseaseAnnotationDAO() {
		super(AlleleDiseaseAnnotation.class);
	}

	public List<Long> findAllAnnotationIds(String speciesName) {
		Query jpqlQuery = entityManager.createQuery("SELECT annotation.id FROM AlleleDiseaseAnnotation annotation WHERE annotation.subject.taxon.name LIKE CONCAT(:speciesName, '%')");
		jpqlQuery.setParameter("speciesName", speciesName);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
