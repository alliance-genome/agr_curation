package org.alliancegenome.curation_api.dao.associations.alleleAssociations;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;

@ApplicationScoped
public class AlleleGeneAssociationDAO extends BaseSQLDAO<AlleleGeneAssociation> {

	protected AlleleGeneAssociationDAO() {
		super(AlleleGeneAssociation.class);
	}

	public List<Long> findAllIdsByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT aga.id FROM AlleleGeneAssociation aga WHERE aga.subject.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<Long>) jpqlQuery.getResultList();
	}

}
