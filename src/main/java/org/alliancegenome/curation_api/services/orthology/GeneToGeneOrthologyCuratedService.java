package org.alliancegenome.curation_api.services.orthology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyCuratedDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class GeneToGeneOrthologyCuratedService extends BaseEntityCrudService<GeneToGeneOrthologyCurated, GeneToGeneOrthologyCuratedDAO> {

	@Inject
	GeneToGeneOrthologyCuratedDAO geneToGeneOrthologyCuratedDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneOrthologyCuratedDAO);
	}

}
