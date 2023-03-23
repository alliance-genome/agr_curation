package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {

	protected AlleleDAO() {
		super(Allele.class);
	}

	public List<String> findAllCuriesByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT allele.curie FROM Allele allele WHERE allele.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<String>) jpqlQuery.getResultList();
	}

	public List<Long> findReferencingDiseaseAnnotationIds(String alleleCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT da.id FROM DiseaseAnnotation da WHERE da.diseaseGeneticModifier.curie = :alleleCurie");
		jpqlQuery.setParameter("alleleCurie", alleleCurie);
		List<Long> results = (List<Long>) jpqlQuery.getResultList();

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AlleleDiseaseAnnotation ada WHERE ada.subject.curie = :alleleCurie");
		jpqlQuery.setParameter("alleleCurie", alleleCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.inferredAllele.curie = :alleleCurie OR ada.assertedAllele.curie = :alleleCurie");
		jpqlQuery.setParameter("alleleCurie", alleleCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		return results;
	}

}
