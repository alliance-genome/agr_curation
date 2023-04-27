package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.OntologyTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class OntologyTermService extends BaseOntologyTermService<OntologyTerm, OntologyTermDAO> {

	@Inject
	OntologyTermDAO ontologyTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ontologyTermDAO);
	}

}
