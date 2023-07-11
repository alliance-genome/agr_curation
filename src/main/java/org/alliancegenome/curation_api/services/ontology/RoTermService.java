package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.RoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ROTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class RoTermService extends BaseOntologyTermService<ROTerm, RoTermDAO> {

	@Inject
	RoTermDAO roTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(roTermDAO);
	}

}
