package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.PhenotypeTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PhenotypeTermService extends BaseOntologyTermService<PhenotypeTerm, PhenotypeTermDAO> {

	@Inject
	PhenotypeTermDAO phenotypeTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(phenotypeTermDAO);
	}

}
