package org.alliancegenome.curation_api.controllers.crud.orthology;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyDAO;
import org.alliancegenome.curation_api.interfaces.crud.orthology.GeneToGeneOrthologyCrudInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneOrthologyCrudController extends BaseEntityCrudController<GeneToGeneOrthologyService, GeneToGeneOrthology, GeneToGeneOrthologyDAO> implements GeneToGeneOrthologyCrudInterface {

	@Inject
	GeneToGeneOrthologyService geneToGeneOrthologyService;

	@Override
	@PostConstruct
	public void init() {
		setService(geneToGeneOrthologyService);
	}

}
