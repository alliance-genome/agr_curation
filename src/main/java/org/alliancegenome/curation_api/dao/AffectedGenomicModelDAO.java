package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;

@ApplicationScoped
public class AffectedGenomicModelDAO extends BaseSQLDAO<AffectedGenomicModel> {

	protected AffectedGenomicModelDAO() {
		super(AffectedGenomicModel.class);
	}

	public List<String> findAllCuriesByTaxon(String taxonId) {
		Query jpqlQuery = entityManager.createQuery("SELECT agm.curie FROM AffectedGenomicModel agm WHERE agm.taxon.curie=:taxonId");
		jpqlQuery.setParameter("taxonId", taxonId);
		return (List<String>) jpqlQuery.getResultList();
	}
	
	@Transactional
	public void deleteReferencingDiseaseAnnotations(List<String> agmCuries) {
		Query jpqlQuery = entityManager.createQuery("SELECT da.id FROM DiseaseAnnotation da WHERE da.diseaseGeneticModifier.curie IN (:agmCuries)");
		jpqlQuery.setParameter("agmCuries", agmCuries);
		List<String> results = (List<String>)jpqlQuery.getResultList();
		
		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.subject.curie IN (:agmCuries)");
		jpqlQuery.setParameter("agmCuries", agmCuries);
		results.addAll((List<String>) jpqlQuery.getResultList());
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_conditionrelation WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_ecoterm WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_note WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
		
		jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_vocabularyterm WHERE diseaseannotation_id IN (:ids)");
		jpqlQuery.setParameter("ids", results);
		jpqlQuery.executeUpdate();
	}
	
}
