package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ChemicalTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.services.ontology.ChemicalTermService;

@RequestScoped
public class ChemicalTermCrudController extends BaseOntologyTermController<ChemicalTermService, ChemicalTerm, ChemicalTermDAO> implements ChemicalTermCrudInterface {

	@Inject ChemicalTermService chemicalTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(chemicalTermService, ChemicalTerm.class);
	}

}
