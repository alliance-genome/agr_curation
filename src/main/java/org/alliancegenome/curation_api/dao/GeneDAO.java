package org.alliancegenome.curation_api.dao;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Gene;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

	protected GeneDAO() {
		super(Gene.class);
	}

	public Gene getByIdOrCurie(String id) {
		return find(id);
	}

	public List<String> findAllCuriesByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT gene.curie FROM Gene gene WHERE gene.dataProvider.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<String>) jpqlQuery.getResultList();
	}

	public List<Long> findReferencingDiseaseAnnotations(String geneCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT da.id FROM DiseaseAnnotation da WHERE da.diseaseGeneticModifier.curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		List<Long> results = (List<Long>) jpqlQuery.getResultList();

		jpqlQuery = entityManager.createQuery("SELECT gda.id FROM GeneDiseaseAnnotation gda WHERE gda.subject.curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.inferredGene.curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AlleleDiseaseAnnotation ada WHERE ada.inferredGene.curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_gene gda WHERE with_curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		jpqlQuery = entityManager.createNativeQuery("SELECT agmdiseaseannotation_id FROM agmdiseaseannotation_gene gda WHERE assertedgenes_curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		jpqlQuery = entityManager.createNativeQuery("SELECT allelediseaseannotation_id FROM allelediseaseannotation_gene gda WHERE assertedgenes_curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		for(BigInteger nativeResult : (List<BigInteger>) jpqlQuery.getResultList())
			results.add(nativeResult.longValue());

		return results;
	}

}
