package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.RsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.RSTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class RsTermService extends BaseOntologyTermService<RSTerm, RsTermDAO> {

	@Inject
	RsTermDAO rsTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(rsTermDAO);
	}

}
