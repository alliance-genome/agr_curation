package org.alliancegenome.curation_api.dao.orthology;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;

@ApplicationScoped
public class GeneToGeneOrthologyGeneratedDAO extends BaseSQLDAO<GeneToGeneOrthologyGenerated> {

	protected GeneToGeneOrthologyGeneratedDAO() {
		super(GeneToGeneOrthologyGenerated.class);
	}

	public List<Object[]> findAllOrthologyPairsBySubjectGeneDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT subjectGene.curie, objectGene.curie FROM GeneToGeneOrthologyGenerated WHERE subjectGene.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<Object[]>) jpqlQuery.getResultList();
	}
}