package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBBTTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class WbbtTermService extends BaseOntologyTermService<WBBTTerm, WbbtTermDAO> {

	@Inject WbbtTermDAO wbbtTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(wbbtTermDAO);
	}
	
}
