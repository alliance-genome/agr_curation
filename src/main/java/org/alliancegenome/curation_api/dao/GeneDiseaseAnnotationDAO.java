package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;

@ApplicationScoped
public class GeneDiseaseAnnotationDAO extends BaseSQLDAO<GeneDiseaseAnnotation> {

	protected GeneDiseaseAnnotationDAO() {
		super(GeneDiseaseAnnotation.class);
	}

	public List<Long> findAllAnnotationIds(String speciesName) {
		Query jpqlQuery = entityManager.createQuery("SELECT annotation.id FROM GeneDiseaseAnnotation annotation WHERE annotation.subject.taxon.name LIKE CONCAT(:speciesName, '%')");
		jpqlQuery.setParameter("speciesName", speciesName);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
