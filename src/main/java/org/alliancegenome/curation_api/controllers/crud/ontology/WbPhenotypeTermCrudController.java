package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.WbPhenotypeTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.WbPhenotypeTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;
import org.alliancegenome.curation_api.services.ontology.WbPhenotypeTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class WbPhenotypeTermCrudController extends BaseOntologyTermController<WbPhenotypeTermService, WBPhenotypeTerm, WbPhenotypeTermDAO> implements WbPhenotypeTermCrudInterface {

	@Inject
	WbPhenotypeTermService wbPhenotypeTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(wbPhenotypeTermService, WBPhenotypeTerm.class);
	}

}
