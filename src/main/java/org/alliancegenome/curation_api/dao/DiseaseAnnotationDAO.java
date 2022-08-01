package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

	@Inject NoteDAO noteDAO;
	
	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}

	public void deleteAttachedNote(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM diseaseannotation_note WHERE relatednotes_id = '" + id + "'");
		jpqlQuery.executeUpdate();
		
		noteDAO.remove(id);
	}
}
