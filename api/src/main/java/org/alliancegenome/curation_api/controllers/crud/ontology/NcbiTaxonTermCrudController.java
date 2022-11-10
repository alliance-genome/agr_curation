package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.NcbiTaxonTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;

@RequestScoped
public class NcbiTaxonTermCrudController extends BaseOntologyTermController<NcbiTaxonTermService, NCBITaxonTerm, NcbiTaxonTermDAO> implements NcbiTaxonTermCrudInterface {

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(ncbiTaxonTermService, NCBITaxonTerm.class);
	}

}
