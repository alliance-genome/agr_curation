package org.alliancegenome.curation_api.dao;

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

	public List<Long> findAllAnnotationIdsByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT annotation.id FROM AGMDiseaseAnnotation annotation WHERE annotation.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
