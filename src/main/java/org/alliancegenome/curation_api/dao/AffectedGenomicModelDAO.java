package org.alliancegenome.curation_api.dao;

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

	public List<String> findAllCuriesByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT agm.curie FROM AffectedGenomicModel agm WHERE agm.dataProvider.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<String>) jpqlQuery.getResultList();
	}

	public List<Long> findReferencingDiseaseAnnotations(String agmCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT da.id FROM DiseaseAnnotation da WHERE da.diseaseGeneticModifier.curie = :agmCurie");
		jpqlQuery.setParameter("agmCurie", agmCurie);
		List<Long> results = (List<Long>) jpqlQuery.getResultList();

		jpqlQuery = entityManager.createQuery("SELECT ada.id FROM AGMDiseaseAnnotation ada WHERE ada.subject.curie = :agmCurie");
		jpqlQuery.setParameter("agmCurie", agmCurie);
		results.addAll((List<Long>) jpqlQuery.getResultList());

		return results;
	}

}
