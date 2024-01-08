package org.alliancegenome.curation_api.dao.orthology;

import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

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

	public List<Object[]> findAllOrthologyPairsBySubjectGeneDataProviderAndTaxon(String dataProvider, String taxonCurie) {
		Query jpqlQuery = entityManager.createQuery("SELECT subjectGene.curie, objectGene.curie FROM GeneToGeneOrthologyGenerated WHERE subjectGene.dataProvider.sourceOrganization.abbreviation = :dataProvider AND subjectGene.taxon.curie = :taxonCurie");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		jpqlQuery.setParameter("taxonCurie", taxonCurie);
		return (List<Object[]>) jpqlQuery.getResultList();
	}
}