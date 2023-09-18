package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Construct;

@ApplicationScoped
public class ConstructDAO extends BaseSQLDAO<Construct> {
	
	@Inject
	NoteDAO noteDAO;

	protected ConstructDAO() {
		super(Construct.class);
	}

	public List<String> findAllIdsByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT construct.id FROM Construct construct WHERE construct.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<String>) jpqlQuery.getResultList();
	}

}
