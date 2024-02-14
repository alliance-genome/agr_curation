package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Gene;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

	protected GeneDAO() {
		super(Gene.class);
	}

	public List<Long> findReferencingDiseaseAnnotations(Long geneId) {
		Query jpqlQuery = entityManager.createQuery("SELECT gda.id FROM GeneDiseaseAnnotation gda WHERE gda.subjectBiologicalEntity.id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		List<Long> results = (List<Long>) jpqlQuery.getResultList();

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.inferredGene.id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AlleleDiseaseAnnotation ada WHERE ada.inferredGene.id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_gene gda WHERE with_id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		jpqlQuery = entityManager.createNativeQuery("SELECT agmdiseaseannotation_id FROM agmdiseaseannotation_gene gda WHERE assertedgenes_id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		jpqlQuery = entityManager.createNativeQuery("SELECT allelediseaseannotation_id FROM allelediseaseannotation_gene gda WHERE assertedgenes_id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());
		
		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_biologicalentity db WHERE diseasegeneticmodifiers_id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());
		
		return results;
	}

	public List<Long> findReferencingOrthologyPairs(Long geneId) {
		Query jpqlQuery = entityManager.createQuery("SELECT o.id FROM GeneToGeneOrthology o WHERE o.subjectGene.id = :geneId OR o.objectGene.id = :geneId");
		jpqlQuery.setParameter("geneId", geneId);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
