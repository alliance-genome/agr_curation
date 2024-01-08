package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.PhenotypeTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.PhenotypeTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PhenotypeTermCrudController extends BaseOntologyTermController<PhenotypeTermService, PhenotypeTerm, PhenotypeTermDAO> implements PhenotypeTermCrudInterface {

	@Inject
	PhenotypeTermService phenotypeTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(phenotypeTermService, PhenotypeTerm.class);
	}

}
