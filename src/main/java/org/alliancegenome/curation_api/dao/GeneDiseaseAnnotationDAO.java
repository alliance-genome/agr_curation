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

	public List<String> findAllAnnotationIds(String taxonID) {
		Query jpqlQuery = entityManager.createQuery("SELECT annotation.uniqueId FROM GeneDiseaseAnnotation annotation WHERE annotation.subject.taxon.curie=:taxonId");
		jpqlQuery.setParameter("taxonId", taxonID);
		return (List<String>) jpqlQuery.getResultList();
	}
	
}
