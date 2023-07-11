package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.BspoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.BSPOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BspoTermService extends BaseOntologyTermService<BSPOTerm, BspoTermDAO> {

	@Inject
	BspoTermDAO bspoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bspoTermDAO);
	}

}
