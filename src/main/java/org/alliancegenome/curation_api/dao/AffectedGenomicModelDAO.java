package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;

@ApplicationScoped
public class AffectedGenomicModelDAO extends BaseSQLDAO<AffectedGenomicModel> {

	protected AffectedGenomicModelDAO() {
		super(AffectedGenomicModel.class);
	}
	
	public List<Long> findReferencingDiseaseAnnotations(String agmCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.subject.curie = :agmCurie");
		jpqlQuery.setParameter("agmCurie", agmCurie);
		List<Long> results = (List<Long>) jpqlQuery.getResultList();
		
		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_biologicalentity db WHERE diseasegeneticmodifiers_curie = :agmCurie");
		jpqlQuery.setParameter("agmCurie", agmCurie);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		return results;
	}

}
