package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.OntologyTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class OntologyTermService extends BaseOntologyTermService<OntologyTerm, OntologyTermDAO> {

	@Inject OntologyTermDAO ontologyTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ontologyTermDAO);
	}
	
}
