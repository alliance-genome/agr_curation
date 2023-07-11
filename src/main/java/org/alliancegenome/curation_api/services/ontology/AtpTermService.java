package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.AtpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AtpTermService extends BaseOntologyTermService<ATPTerm, AtpTermDAO> {

	@Inject
	AtpTermDAO atpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(atpTermDAO);
	}

}
