package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.OntologyTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class OntologyTermService extends BaseOntologyTermService<OntologyTerm, OntologyTermDAO> {

	@Inject OntologyTermDAO ontologyTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ontologyTermDAO);
	}
	
}
