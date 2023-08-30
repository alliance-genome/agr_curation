package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;

@ApplicationScoped
public class AnnotationDAO extends BaseSQLDAO<Annotation> {

	@Inject
	NoteDAO noteDAO;

	protected AnnotationDAO() {
		super(Annotation.class);
	}

	public void deleteAttachedNote(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM annotation_note WHERE relatednotes_id = '" + id + "'");
		jpqlQuery.executeUpdate();

		noteDAO.remove(id);
	}
}
