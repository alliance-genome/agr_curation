package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.AtpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class AtpTermService extends BaseOntologyTermService<ATPTerm, AtpTermDAO> {

	@Inject AtpTermDAO atpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(atpTermDAO);
	}

}
