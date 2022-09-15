package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;

@ApplicationScoped
public class GenomicEntityDAO extends BaseSQLDAO<GenomicEntity> {

	@Inject SynonymDAO synonymDAO;
	@Inject CrossReferenceDAO crossReferenceDAO;
	
	protected GenomicEntityDAO() {
		super(GenomicEntity.class);
	}

	public void deleteAttachedSynonym(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM genomicentity_synonym WHERE synonyms_id = '" + id + "'");
		jpqlQuery.executeUpdate();
		
		synonymDAO.remove(id);
	}

	public void deleteAttachedCrossReference(String curie) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM genomicentity_crossreference WHERE crossreferences_curie = '" + curie + "'");
		jpqlQuery.executeUpdate();
		
		crossReferenceDAO.remove(curie);
	}
}
