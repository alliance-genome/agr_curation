package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.transaction.Transactional;

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

	public List<String> findAllCuriesByTaxon(String taxonId) {
		Query jpqlQuery = entityManager.createQuery("SELECT gene.curie FROM Gene gene WHERE gene.taxon.curie=:taxonId");
		jpqlQuery.setParameter("taxonId", taxonId);
		return (List<String>) jpqlQuery.getResultList();
	}
	
	@Transactional
	public void deleteGeneAndReferencingDiseaseAnnotations(String geneCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT da.id FROM DiseaseAnnotation da WHERE da.diseaseGeneticModifier.curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		List<String> results = (List<String>)jpqlQuery.getResultList();
		
		jpqlQuery = entityManager.createQuery("SELECT gda.id FROM GeneDiseaseAnnotation gda WHERE gda.subject.curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		results.addAll((List<String>) jpqlQuery.getResultList());
		
		jpqlQuery = entityManager.createNativeQuery("SELECT diseaseannotation_id FROM diseaseannotation_gene gda WHERE with_curie = :geneCurie");
		jpqlQuery.setParameter("geneCurie", geneCurie);
		results.addAll((List<String>) jpqlQuery.getResultList());
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_conditionrelation WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_ecoterm WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_gene WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_note WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_vocabularyterm WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		remove(geneCurie);
	}
	

}
