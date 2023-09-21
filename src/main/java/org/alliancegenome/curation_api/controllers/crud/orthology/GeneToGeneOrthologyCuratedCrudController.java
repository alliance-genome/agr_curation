package org.alliancegenome.curation_api.controllers.crud.orthology;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyCuratedDAO;
import org.alliancegenome.curation_api.interfaces.crud.orthology.GeneToGeneOrthologyCuratedCrudInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyCuratedService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneOrthologyCuratedCrudController extends BaseEntityCrudController<GeneToGeneOrthologyCuratedService, GeneToGeneOrthologyCurated, GeneToGeneOrthologyCuratedDAO> implements GeneToGeneOrthologyCuratedCrudInterface {

	@Inject
	GeneToGeneOrthologyCuratedService geneToGeneOrthologyCuratedService;

	@Override
	@PostConstruct
	public void init() {
		setService(geneToGeneOrthologyCuratedService);
	}

}
