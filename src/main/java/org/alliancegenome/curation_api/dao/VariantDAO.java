package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {
	
	@Inject
	NoteDAO noteDAO;

	protected VariantDAO() {
		super(Variant.class);
	}

	public void deleteAttachedNote(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM variant_note WHERE relatednotes_id = '" + id + "'");
		jpqlQuery.executeUpdate();

		noteDAO.remove(id);
	}

}
