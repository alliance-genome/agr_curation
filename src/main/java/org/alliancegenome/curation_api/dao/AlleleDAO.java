package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.ArrayList;
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

	public List<Long> findReferencingDiseaseAnnotationIds(Long alleleId) {
		List<Long> results = new ArrayList<>();
		Query jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AlleleDiseaseAnnotation ada WHERE ada.subjectBiologicalEntity.id = :alleleId");
		jpqlQuery.setParameter("alleleId", alleleId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.inferredAllele.id = :alleleId OR ada.assertedAllele.id = :alleleId");
		jpqlQuery.setParameter("alleleId", alleleId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());
		
		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_biologicalentity db WHERE diseasegeneticmodifiers_id = :alleleId");
		jpqlQuery.setParameter("alleleId", alleleId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		return results;
	}

}
