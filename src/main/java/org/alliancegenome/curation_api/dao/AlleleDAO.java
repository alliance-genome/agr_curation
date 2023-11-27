package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {
	
	protected AlleleDAO() {
		super(Allele.class);
	}

	public List<Long> findReferencingDiseaseAnnotationIds(String alleleCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AlleleDiseaseAnnotation ada WHERE ada.subject.curie = :alleleCurie");
		jpqlQuery.setParameter("alleleCurie", alleleCurie);
		List<Long> results = (List<Long>) jpqlQuery.getResultList();

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.inferredAllele.curie = :alleleCurie OR ada.assertedAllele.curie = :alleleCurie");
		jpqlQuery.setParameter("alleleCurie", alleleCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());
		
		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_biologicalentity db WHERE diseasegeneticmodifiers_curie = :alleleCurie");
		jpqlQuery.setParameter("alleleCurie", alleleCurie);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		return results;
	}

}
