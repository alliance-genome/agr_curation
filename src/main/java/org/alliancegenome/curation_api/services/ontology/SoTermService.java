package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;

@RequestScoped
public class SoTermService extends BaseOntologyTermService<SOTerm, SoTermDAO> {

	@Inject SoTermDAO soTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(soTermDAO);
	}
	
}
