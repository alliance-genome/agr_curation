package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class AffectedGenomicModelDAO extends BaseSQLDAO<AffectedGenomicModel> {

	protected AffectedGenomicModelDAO() {
		super(AffectedGenomicModel.class);
	}
	
	public List<Long> findReferencingDiseaseAnnotations(Long agmId) {
		List<Long> results = new ArrayList<>();
		
		Query jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.subject.id = :agmId");
		jpqlQuery.setParameter("agmId", agmId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());
		
		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_biologicalentity db WHERE diseasegeneticmodifiers_id = :agmId");
		jpqlQuery.setParameter("agmId", agmId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		return results;
	}

}
