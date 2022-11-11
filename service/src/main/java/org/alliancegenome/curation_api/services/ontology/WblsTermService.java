package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.WblsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBLSTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class WblsTermService extends BaseOntologyTermService<WBLSTerm, WblsTermDAO> {

	@Inject WblsTermDAO wblsTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(wblsTermDAO);
	}
	
}
