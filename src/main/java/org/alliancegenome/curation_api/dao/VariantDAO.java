package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {
	
	protected VariantDAO() {
		super(Variant.class);
	}

	public List<Long> findReferencingDiseaseAnnotationIds(Long variantId) {
		List<Long> results = new ArrayList<>();
		Query jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_biologicalentity db WHERE diseasegeneticmodifiers_id = :variantId");
		jpqlQuery.setParameter("variantId", variantId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		return results;
	}
}
