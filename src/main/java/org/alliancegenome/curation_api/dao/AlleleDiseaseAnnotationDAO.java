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

	public List<Long> findAllAnnotationIdsByDataProvider(String sourceOrg) {
		return findAllAnnotationIdsByDataProvider(sourceOrg, null);
	}

	public List<Long> findAllAnnotationIdsByDataProvider(String sourceOrg, String taxonCurie) {
		String qlString = "SELECT annotation.id";
		qlString += " FROM AlleleDiseaseAnnotation annotation";
		qlString += " WHERE annotation.subject.dataProvider.sourceOrganization.abbreviation = :sourceOrg";
		if ( taxonCurie != null && !taxonCurie.isEmpty() && !taxonCurie.isBlank() ){
			qlString += " AND annotation.subject.taxon.curie = :taxonCurie";
		}

		Query jpqlQuery = entityManager.createQuery(qlString);
		jpqlQuery.setParameter("sourceOrg", sourceOrg);
		if ( taxonCurie != null && !taxonCurie.isEmpty() && !taxonCurie.isBlank() ){
			jpqlQuery.setParameter("taxonCurie", taxonCurie);
		}

		return (List<Long>) jpqlQuery.getResultList();
	}

}
