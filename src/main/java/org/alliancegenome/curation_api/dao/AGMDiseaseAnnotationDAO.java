package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;

@ApplicationScoped
public class AGMDiseaseAnnotationDAO extends BaseSQLDAO<AGMDiseaseAnnotation> {

	protected AGMDiseaseAnnotationDAO() {
		super(AGMDiseaseAnnotation.class);
	}

	public List<Long> findAllAnnotationIds(String taxonID) {
		Query jpqlQuery = entityManager.createQuery("SELECT annotation.id FROM AGMDiseaseAnnotation annotation WHERE annotation.subject.taxon.curie=:taxonId");
		jpqlQuery.setParameter("taxonId", taxonID);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
