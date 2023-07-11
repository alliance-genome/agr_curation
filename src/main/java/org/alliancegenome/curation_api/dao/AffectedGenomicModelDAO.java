package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
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

	public List<String> findAllCuriesByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT agm.curie FROM AffectedGenomicModel agm WHERE agm.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<String>) jpqlQuery.getResultList();
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
