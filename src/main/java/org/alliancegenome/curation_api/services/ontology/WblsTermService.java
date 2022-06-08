package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.WblsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBlsTerm;

@RequestScoped
public class WblsTermService extends BaseOntologyTermService<WBlsTerm, WblsTermDAO> {

	@Inject WblsTermDAO wblsTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(wblsTermDAO);
	}
	
}
