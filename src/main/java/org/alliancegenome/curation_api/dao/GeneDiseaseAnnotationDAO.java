package org.alliancegenome.curation_api.dao;

import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneDiseaseAnnotationDAO extends BaseSQLDAO<GeneDiseaseAnnotation> {

	protected GeneDiseaseAnnotationDAO() {
		super(GeneDiseaseAnnotation.class);
	}

	public List<Long> findAllAnnotationIdsByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT annotation.id FROM GeneDiseaseAnnotation annotation WHERE annotation.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
