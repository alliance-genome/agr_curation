package org.alliancegenome.curation_api.dao.associations.constructAssociations;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;

@ApplicationScoped
public class ConstructGenomicEntityAssociationDAO extends BaseSQLDAO<ConstructGenomicEntityAssociation> {

	@Inject
	NoteDAO noteDAO;
	
	protected ConstructGenomicEntityAssociationDAO() {
		super(ConstructGenomicEntityAssociation.class);
	}

	public void deleteAttachedNote(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM constructgenomicentityassociation_note WHERE relatednotes_id = '" + id + "'");
		jpqlQuery.executeUpdate();

		noteDAO.remove(id);
	}

}
