package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ChemicalTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.services.ontology.ChemicalTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ChemicalTermCrudController extends BaseOntologyTermController<ChemicalTermService, ChemicalTerm, ChemicalTermDAO> implements ChemicalTermCrudInterface {

	@Inject
	ChemicalTermService chemicalTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(chemicalTermService, ChemicalTerm.class);
	}

}
